package ru.netology.entity.card;

import org.joda.time.*;

import java.util.Objects;

public class Card {
    private final String number;
    private final LocalDate validTill;
    private final String CVV;

    public Card(String number, LocalDate validTill, String CVV) {
        this.number = number;
        this.validTill = validTill;
        this.CVV = CVV;
    }

    public Card(String number) {
        this.number = number;
        this.validTill = null;
        this.CVV = null;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getValidTill() {
        return validTill;
    }

    public String getCVV() {
        return CVV;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(number, card.number) && Objects.equals(validTill, card.validTill) && Objects.equals(CVV, card.CVV);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, validTill, CVV);
    }
}
