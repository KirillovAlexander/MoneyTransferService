package ru.netology.entity.card;

import java.math.BigDecimal;

public class CardBalance {
    private final Card card;
    private BigDecimal balance;

    public CardBalance(Card card, BigDecimal balance) {
        this.card = card;
        this.balance = balance;
    }

    public Card getCard() {
        return card;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCardNumber() {
        return card.getNumber();
    }
}
