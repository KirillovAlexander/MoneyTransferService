package ru.netology.dto;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
public class OperationDTO {
    @NotBlank
    @Size(min = 16, max = 16)
    private String cardFromNumber;
    @NotBlank
    @Size(min = 5, max = 5)
    private String cardFromValidTill;
    @NotBlank
    @Size(min = 3, max = 3)
    private String cardFromCVV;
    @NotBlank
    @Size(min = 16, max = 16)
    private String cardToNumber;
    private AmountDTO amount;

    public OperationDTO(String cardFromNumber, String cardFromValidTill, String cardFromCVV, String cardToNumber, AmountDTO amount) {
        this.cardFromNumber = cardFromNumber;
        this.cardFromValidTill = cardFromValidTill;
        this.cardFromCVV = cardFromCVV;
        this.cardToNumber = cardToNumber;
        this.amount = amount;
    }

    public String getCardFromNumber() {
        return cardFromNumber;
    }

    public String getCardFromValidTill() {
        return cardFromValidTill;
    }

    public String getCardFromCVV() {
        return cardFromCVV;
    }

    public String getCardToNumber() {
        return cardToNumber;
    }

    public long getAmountValue() {
        return amount.getValue();
    }

    public void setAmount(AmountDTO amount) {
        this.amount = amount;
    }

}
