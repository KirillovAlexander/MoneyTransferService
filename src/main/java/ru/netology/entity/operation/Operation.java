package ru.netology.entity.operation;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import ru.netology.dto.OperationDTO;
import ru.netology.entity.card.Card;

import java.math.BigDecimal;
import java.util.UUID;

public class Operation {

    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("MM/yy");

    private final UUID operationId;
    private final Card cardFrom;
    private final Card cardTo;
    private final LocalDate date;
    private final BigDecimal amount;
    private BigDecimal commission;
    private String verificationCode;
    private boolean isCompleted;

    public Operation(Card cardFrom, Card cardTo, BigDecimal amount) {
        this.cardFrom = cardFrom;
        this.cardTo = cardTo;
        this.amount = amount;
        this.operationId = UUID.randomUUID();
        this.date = LocalDate.now();
    }

    public static Operation getOperationFromOperationDTO(OperationDTO operationDTO) {
        Card cardFrom = new Card(operationDTO.getCardFromNumber(),
                getDate(operationDTO.getCardFromValidTill()),
                operationDTO.getCardFromCVV());
        Card cardTo = new Card(operationDTO.getCardToNumber());
        BigDecimal amount = BigDecimal.valueOf(operationDTO.getAmountValue());
        return new Operation(cardFrom, cardTo, amount);
    }

    private static LocalDate getDate(String date) {
        return DATE_FORMATTER.parseLocalDate(date);
    }

    public Card getCardFrom() {
        return cardFrom;
    }

    public Card getCardTo() {
        return cardTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public UUID getOperationId() {
        return operationId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getAmountWithCommission() {
        return amount.add(commission);
    }

    public String info() {
        return cardFrom.getNumber() + " -> " + cardTo.getNumber() + ", amount: " + amount + " with commission: " + commission;
    }
}
