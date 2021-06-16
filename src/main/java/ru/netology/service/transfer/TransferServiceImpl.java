package ru.netology.service.transfer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.netology.dto.OperationDTO;
import ru.netology.entity.card.Card;
import ru.netology.entity.card.CardBalance;
import ru.netology.entity.operation.Operation;
import ru.netology.exceptions.ErrorInputData;
import ru.netology.exceptions.ErrorTransfer;
import ru.netology.repository.CardInfoRepository;
import ru.netology.service.commission.CommissionService;
import ru.netology.service.verification.VerificationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TransferServiceImpl implements TransferService {
    private final CardInfoRepository repository;
    private final VerificationService verificationService;
    private final CommissionService commissionService;
    private final ReentrantLock locker = new ReentrantLock(true);

    private final static Logger logger = Logger.getLogger(TransferServiceImpl.class);

    @Value("${rounding.scale}")
    private int SCALE;
    @Value("${rounding.mode}")
    private RoundingMode ROUNDING_MODE;

    public TransferServiceImpl(CardInfoRepository repository, VerificationService verificationService, CommissionService commissionService) {
        this.repository = repository;
        this.verificationService = verificationService;
        this.commissionService = commissionService;
    }

    @Override
    public UUID transfer(Operation operation) {
        //Check and return id
        if (operationValid(operation)) {
            operation.setVerificationCode(verificationService.getNewVerificationCode());
            repository.addOperation(operation);
            return operation.getOperationId();
        }
        throw new ErrorTransfer("Что-то пошло не так!");
    }

    private boolean operationValid(Operation operation) {
        //Get cards data from operation
        Card cardFrom = operation.getCardFrom();
        Card cardTo = operation.getCardTo();
        //set commission value
        setCommission(operation);
        //Check if cards are equal
        if (sameCards(cardFrom, cardTo)) throw new ErrorTransfer("Нельзя переводить деньги между одной картой.");
        //Get card balance from repo
        Optional<CardBalance> optionalCardBalanceFrom = repository.getCardBalanceByNumber(cardFrom.getNumber());
        //Check `card from` and `card to`
        return cardBalanceFromValid(optionalCardBalanceFrom, cardFrom, operation.getAmountWithCommission()) && cardBalanceToValid(cardTo);
    }

    private boolean cardBalanceFromValid(Optional<CardBalance> optCardBalance, Card cardFrom, BigDecimal transferAmount) {
        CardBalance cardBalanceFrom = optCardBalance.orElseThrow(() -> new ErrorInputData("Неверные данные вашей карты"));
        Card validCard = cardBalanceFrom.getCard();
        if (!cardFrom.getValidTill().equals(validCard.getValidTill()) || !cardFrom.getCVV().equals(validCard.getCVV()))
            throw new ErrorInputData("Неверные данные вашей карты");
        BigDecimal balance = cardBalanceFrom.getBalance();
        if (balance.compareTo(transferAmount) < 0)
            throw new ErrorTransfer("Недостаточно средств на счёте.");
        return true;
    }

    private boolean cardBalanceToValid(Card card) {
        Optional<CardBalance> optionalCardBalanceTo = repository.getCardBalanceByNumber(card.getNumber());
        if (optionalCardBalanceTo.isEmpty()) throw new ErrorInputData("Неверные данные вашей карты");
        return true;
    }

    @Override
    public UUID confirmOperation(String operationId, String code) {
        Operation operation = getOperationFromRepo(operationId);
        checkCompleteStatus(operation);
        //Get card balance from repo
        Optional<CardBalance> optionalCardBalance = repository.getCardBalanceByNumber(operation.getCardFrom().getNumber());
        CardBalance cardBalanceFrom = optionalCardBalance.orElseThrow(() -> new ErrorInputData("Неверные данные вашей карты"));
        BigDecimal balanceFrom = cardBalanceFrom.getBalance();
        //Check `card to`
        Optional<CardBalance> optionalCardBalanceTo = repository.getCardBalanceByNumber(operation.getCardTo().getNumber());
        CardBalance cardBalanceTo = optionalCardBalanceTo.orElseThrow(() -> new ErrorInputData("Неверные данные карты-получателя"));
        //Set commission and check `card from` balance
        if (balanceFrom.compareTo(operation.getAmountWithCommission()) < 0)
            throw new ErrorTransfer("Недостаточно средств на счёте.");
        //Check verification code
        checkValidationCode(code, operation);
        //Do operation
        makeTransfer(cardBalanceFrom, cardBalanceTo, operation);
        return operation.getOperationId();
    }

    private void setCommission(Operation operation) {
        operation.setCommission(operation.getAmount().multiply(commissionService.getPct()).setScale(SCALE, ROUNDING_MODE));
    }

    private boolean sameCards(Card cardFrom, Card cardTo) {
        return cardFrom.getNumber().equals(cardTo.getNumber());
    }

    private void makeTransfer(CardBalance cardBalanceFrom, CardBalance cardBalanceTo, Operation operation) {
        locker.lock();
        try {
            repository.subtractBalance(cardBalanceFrom, operation.getAmountWithCommission());
            repository.addBalance(cardBalanceTo, operation.getAmount());
            operation.setCompleted(true);
            logger.info(operation.info());
        } finally {
            locker.unlock();
        }
    }

    private void checkValidationCode(String code, Operation operation) {
        String validCode = operation.getVerificationCode();
        if (!code.equals(validCode)) throw new ErrorTransfer("Подтверждающий код неверный.");
    }

    private void checkCompleteStatus(Operation operation) {
        if (operation.isCompleted()) throw new ErrorTransfer("Операция уже выполнена.");
    }

    private Operation getOperationFromRepo(String operationId) {
        Optional<Operation> optionalOperation = repository.getOperation(UUID.fromString(operationId));
        return optionalOperation.orElseThrow(() -> new ErrorTransfer("Неверный код операции."));
    }

}
