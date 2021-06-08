package ru.netology.dto;

public class AmountDTO {
    private String currency;
    private long value;

    public AmountDTO(String currency, long value) {
        this.currency = currency;
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public long getValue() {
        return value;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setValue(long value) {
        this.value = value;
    }

}
