package ru.netology.dto;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Validated
public class AmountDTO {
    @NotBlank
    private String currency;
    @NotEmpty
    @Min(1)
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
