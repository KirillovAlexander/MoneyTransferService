package ru.netology.service.transfer;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.dto.AmountDTO;
import ru.netology.dto.OperationDTO;
import ru.netology.entity.card.Card;
import ru.netology.entity.card.CardBalance;
import ru.netology.entity.operation.Operation;
import ru.netology.exceptions.ErrorInputData;
import ru.netology.exceptions.ErrorTransfer;
import ru.netology.repository.TransferRepository;
import ru.netology.service.commission.CommissionService;
import ru.netology.service.verification.VerificationService;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class TransferServiceTest {

    private static TransferService service;
    private static final Operation operation;
    private static final Card card;
    private static final String SOME_INCORRECT_ID = "92297218-c7aa-11eb-b8bc-0242ac130003";

    static {
        card = new Card("testNotNull", LocalDate.parse("12/12", DateTimeFormat.forPattern("MM/yy")), "111");
        operation = new Operation(card, card, BigDecimal.valueOf(10));
        operation.setVerificationCode("0000");
    }

    @BeforeAll
    public static void init() {
        CardBalance cardBalance = new CardBalance(card, new BigDecimal(100));
        Optional<CardBalance> optionalCardBalance = Optional.of(cardBalance);

        TransferRepository repository = Mockito.mock(TransferRepository.class);
        Mockito.when(repository.getCardBalanceByNumber("testNotNull"))
                .thenReturn(optionalCardBalance);

        Mockito.when(repository.getCardBalanceByNumber("testNull"))
                .thenReturn(Optional.ofNullable(null));

        Mockito.when(repository.getOperation(operation.getOperationId()))
                .thenReturn(Optional.of(operation));

        Mockito.when(repository.getOperation(UUID.fromString(SOME_INCORRECT_ID)))
                .thenReturn(Optional.ofNullable(null));

        CommissionService commissionService = Mockito.mock(CommissionService.class);
        Mockito.when(commissionService.getPct())
                .thenReturn(BigDecimal.valueOf(0.01));

        VerificationService verificationService = Mockito.mock(VerificationService.class);

        service = new TransferService(repository, verificationService, commissionService);
        setScaleAndRoundingMode(service);
    }

    private static void setScaleAndRoundingMode(TransferService service) {
        try {
            Field roundingMode = service.getClass().getDeclaredField("ROUNDING_MODE");
            roundingMode.setAccessible(true);
            roundingMode.set(service, RoundingMode.HALF_UP);
            Field scale = service.getClass().getDeclaredField("SCALE");
            scale.setAccessible(true);
            scale.setInt(service, 1);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void transferSameCards() {
        OperationDTO operationDTO = new OperationDTO("same", "01/01", "111", "same",
                new AmountDTO("RUR", 10));

        Throwable thrown = assertThrows(ErrorTransfer.class, () -> service.transfer(operationDTO));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void transferIncorrectCardNumber() {
        OperationDTO operationDTO = new OperationDTO("testNull", "01/01", "111", "AnotherCard",
                new AmountDTO("RUR", 10));

        Throwable thrown = assertThrows(ErrorInputData.class, () -> service.transfer(operationDTO));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void transferIncorrectCVV() {
        OperationDTO operationDTO = new OperationDTO("testNotNull", "01/01", "value", "AnotherCard",
                new AmountDTO("RUR", 10));

        Throwable thrown = assertThrows(ErrorInputData.class, () -> service.transfer(operationDTO));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void transferIncorrectDate() {
        OperationDTO operationDTO = new OperationDTO("testNotNull", "01/01", "111", "AnotherCard",
                new AmountDTO("RUR", 10));

        Throwable thrown = assertThrows(ErrorInputData.class, () -> service.transfer(operationDTO));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void transferIncorrectAmount() {
        OperationDTO operationDTO = new OperationDTO("testNotNull", "12/12", "111", "AnotherCard",
                new AmountDTO("RUR", 110));

        Throwable thrown = assertThrows(ErrorTransfer.class, () -> service.transfer(operationDTO));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void confirmOperationIncorrectId() {
        Throwable thrown = assertThrows(ErrorTransfer.class, () -> service.confirmOperation(SOME_INCORRECT_ID, "0000"));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void confirmOperationIncorrectCode() {
        Throwable thrown = assertThrows(ErrorTransfer.class, () -> service.confirmOperation(operation.getOperationId().toString(), "Incorrect"));
        assertNotNull(thrown.getMessage());
    }

    @Test
    void confirmOperation() {
        assertThat(service.confirmOperation(operation.getOperationId().toString(), "0000"), is(equalTo(operation.getOperationId())));
    }

    @Test
    void confirmOperationIncorrectAmount() {
        Operation operationWithIncorrectAmount = new Operation(card, card, BigDecimal.valueOf(110));
        operationWithIncorrectAmount.setVerificationCode("0000");
        Throwable thrown = assertThrows(ErrorTransfer.class, () -> service.confirmOperation(operationWithIncorrectAmount.getOperationId().toString(), "0000"));
        assertNotNull(thrown.getMessage());
    }
}