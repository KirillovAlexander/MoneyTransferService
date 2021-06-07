package ru.netology.repository;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.netology.entity.card.Card;
import ru.netology.entity.card.CardBalance;
import ru.netology.entity.operation.Operation;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTransferRepository implements TransferRepository {
    private final Map<String, CardBalance> cards = new ConcurrentHashMap<>();
    private final Map<UUID, Operation> operationMap = new ConcurrentHashMap<>();

    @Value("${rounding.scale}")
    private int SCALE;
    @Value("${rounding.mode}")
    private RoundingMode ROUNDING_MODE;

    /**
     * Generating test data
     */
    @PostConstruct
    private void init() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/yy");
        Card card1 = new Card("1111111111111111", formatter.parseLocalDate("11/21"), "111");
        Card card2 = new Card("2222222222222222", formatter.parseLocalDate("02/22"), "222");
        Card card3 = new Card("3333333333333333", formatter.parseLocalDate("03/23"), "333");
        CardBalance cardBalance1 = new CardBalance(card1, new BigDecimal(300000000));
        CardBalance cardBalance2 = new CardBalance(card2, new BigDecimal(500000000));
        CardBalance cardBalance3 = new CardBalance(card3, new BigDecimal(100000000));
        cards.put(cardBalance1.getCardNumber(), cardBalance1);
        cards.put(cardBalance2.getCardNumber(), cardBalance2);
        cards.put(cardBalance3.getCardNumber(), cardBalance3);
    }

    @Override
    public Optional<CardBalance> getCardBalanceByNumber(String number) {
        return Optional.ofNullable(cards.get(number));
    }

    @Override
    public void addOperation(Operation operation) {
        operationMap.put(operation.getOperationId(), operation);
    }

    @Override
    public Optional<Operation> getOperation(UUID id) {
        return Optional.ofNullable(operationMap.get(id));
    }

    @Override
    public void addBalance(CardBalance cardBalance, BigDecimal amount) {
        cardBalance.setBalance(cardBalance.getBalance().add(amount).setScale(SCALE, ROUNDING_MODE));
    }

    @Override
    public void subtractBalance(CardBalance cardBalance, BigDecimal amount) {
        cardBalance.setBalance(cardBalance.getBalance().subtract(amount).setScale(SCALE, ROUNDING_MODE));
    }
}
