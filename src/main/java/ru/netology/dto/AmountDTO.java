package ru.netology.dto;

public class AmountDTO {
    private String currency;
    private long value;

    public AmountDTO(String currency, int value) {
        this.currency = currency;
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public long getValue() {
        return value;
    }
}
