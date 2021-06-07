package ru.netology.service.transfer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.netology.dto.OperationDTO;
import ru.netology.entity.card.Card;
import ru.netology.entity.card.CardBalance;
import ru.netology.entity.operation.Operation;
import ru.netology.exceptions.ErrorInputData;
import ru.netology.exceptions.ErrorTransfer;
import ru.netology.repository.TransferRepository;
import ru.netology.service.commission.CommissionService;
import ru.netology.service.verification.VerificationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransferService {
    private final TransferRepository repository;
    private final VerificationService verificationService;
    private final CommissionService commissionService;

    @Value("${rounding.scale}")
    private int SCALE;
    @Value("${rounding.mode}")
    private RoundingMode ROUNDING_MODE;

    public TransferService(TransferRepository repository, VerificationService verificationService, CommissionService commissionService) {
        this.repository = repository;
        this.verificationService = verificationService;
        this.commissionService = commissionService;
    }

    public UUID transfer(OperationDTO operationDTO) {
        //Map DTO to operation
        Operation operation = Operation.getOperationFromOperationDTO(operationDTO);
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
        BigDecimal transferAmount = operation.getAmount();
        //Check if cards are equal
        if (sameCards(cardFrom, cardTo)) throw new ErrorTransfer("Нельзя переводить деньги между одной картой.");
        //Get card balance from repo
        Optional<CardBalance> optionalCardBalanceFrom = repository.getCardBalanceByNumber(cardFrom.getNumber());
        //Check `card from` and `card to`
        return cardBalanceFromValid(optionalCardBalanceFrom, cardFrom, transferAmount) && cardBalanceToValid(cardTo);
    }

    private boolean cardBalanceFromValid(Optional<CardBalance> optCardBalance, Card cardFrom, BigDecimal transferAmount) {
        CardBalance cardBalanceFrom = optCardBalance.orElseThrow(ErrorInputData::new);
        Card validCard = cardBalanceFrom.getCard();
        if (!cardFrom.getValidTill().equals(validCard.getValidTill()) || !cardFrom.getCVV().equals(validCard.getCVV()))
            throw new ErrorInputData();
        BigDecimal balance = cardBalanceFrom.getBalance();
        if (balance.compareTo(getAmountWithCommission(transferAmount)) < 0)
            throw new ErrorTransfer("Недостаточно средств на счёте.");
        return true;
    }

    private boolean cardBalanceToValid(Card card) {
        Optional<CardBalance> optionalCardBalanceTo = repository.getCardBalanceByNumber(card.getNumber());
        if (optionalCardBalanceTo.isEmpty()) throw new ErrorInputData();
        return true;
    }

    public UUID confirmOperation(String operationId, String code) {
        //Get operation from repo
        Optional<Operation> optionalOperation = repository.getOperation(UUID.fromString(operationId));
        Operation operation = optionalOperation.orElseThrow(() -> new ErrorTransfer("Неверный код операции."));
        //Get card balance from repo
        Optional<CardBalance> optionalCardBalance = repository.getCardBalanceByNumber(operation.getCardFrom().getNumber());
        CardBalance cardBalanceFrom = optionalCardBalance.orElseThrow(ErrorInputData::new);
        BigDecimal balanceFrom = cardBalanceFrom.getBalance();
        //Check `card to`
        Optional<CardBalance> optionalCardBalanceTo = repository.getCardBalanceByNumber(operation.getCardTo().getNumber());
        CardBalance cardBalanceTo = optionalCardBalanceTo.orElseThrow(ErrorInputData::new);
        //Check `card from` balance
        if (balanceFrom.compareTo(getAmountWithCommission(operation.getAmount())) < 0)
            throw new ErrorTransfer("Недостаточно средств на счёте.");
        //Check verification code
        String validCode = operation.getVerificationCode();
        if (!code.equals(validCode)) throw new ErrorTransfer("Подтверждающий код неверный.");
        //Do operation
        repository.subtractBalance(cardBalanceFrom, getAmountWithCommission(operation.getAmount()));
        repository.addBalance(cardBalanceTo, operation.getAmount());
        operation.setCompleted(true);

        return operation.getOperationId();
    }


    private BigDecimal getAmountWithCommission(BigDecimal amount) {
        return amount.add(amount.multiply(commissionService.getPct())).setScale(SCALE, ROUNDING_MODE);
    }

    private boolean sameCards(Card cardFrom, Card cardTo) {
        return cardFrom.getNumber().equals(cardTo.getNumber());
    }
}
