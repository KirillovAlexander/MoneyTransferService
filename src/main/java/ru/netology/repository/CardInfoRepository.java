package ru.netology.repository;

import ru.netology.entity.card.CardBalance;
import ru.netology.entity.operation.Operation;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface CardInfoRepository {
    Optional<CardBalance> getCardBalanceByNumber(String number);

    void addOperation(Operation operation);

    Optional<Operation> getOperation(UUID id);

    void addBalance(CardBalance cardBalance, BigDecimal amount);

    void subtractBalance(CardBalance cardBalance, BigDecimal amount);

    void addCardBalance(CardBalance cardBalance);
}
