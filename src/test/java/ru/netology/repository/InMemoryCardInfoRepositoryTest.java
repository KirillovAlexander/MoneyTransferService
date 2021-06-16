package ru.netology.repository;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.entity.card.Card;
import ru.netology.entity.card.CardBalance;
import ru.netology.entity.operation.Operation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

class InMemoryCardInfoRepositoryTest {

    private static CardInfoRepository repository = new InMemoryCardInfoRepository();

    @BeforeAll
    public static void init() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/yy");
        Card card1 = new Card("1111111111111111", formatter.parseLocalDate("11/21"), "111");
        Card card2 = new Card("2222222222222222", formatter.parseLocalDate("02/22"), "222");
        Card card3 = new Card("3333333333333333", formatter.parseLocalDate("03/23"), "333");
        CardBalance cardBalance1 = new CardBalance(card1, new BigDecimal(300000000));
        CardBalance cardBalance2 = new CardBalance(card2, new BigDecimal(500000000));
        CardBalance cardBalance3 = new CardBalance(card3, new BigDecimal(100000000));
        repository.addCardBalance(cardBalance1);
        repository.addCardBalance(cardBalance2);
        repository.addCardBalance(cardBalance3);
        try {
            Field roundingMode = repository.getClass().getDeclaredField("ROUNDING_MODE");
            roundingMode.setAccessible(true);
            roundingMode.set(repository, RoundingMode.HALF_UP);
            Field scale = repository.getClass().getDeclaredField("SCALE");
            scale.setAccessible(true);
            scale.setInt(repository, 1);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
    }

    @Test
    void getCardBalanceByNumber() {
        //when:
        Optional<CardBalance> cardBalance = repository.getCardBalanceByNumber("1111111111111111");
        Optional<CardBalance> cardNullBalance = repository.getCardBalanceByNumber("Some value");

        //then:
        assertThat(cardBalance.get().getCardNumber(), is(equalTo("1111111111111111")));
        assertThat(cardNullBalance.isPresent(), is(equalTo(false)));
    }

    @Test
    void addOperation() {
        //given:
        Card cardFrom = new Card("1", LocalDate.now(), "1");
        Card cardTo = new Card("2", LocalDate.now(), "2");
        BigDecimal amount = BigDecimal.valueOf(1);
        Operation operation = new Operation(cardFrom, cardTo, amount);

        //when:
        repository.addOperation(operation);
        Optional<Operation> optOperation = repository.getOperation(operation.getOperationId());
        Optional<Operation> optNullOperation = repository.getOperation(UUID.randomUUID());

        //then:
        assertThat(operation, is(equalTo(optOperation.get())));
        assertThat(optNullOperation.isEmpty(), is(equalTo(true)));
    }

    @Test
    void getOperation() {
        //given:
        Card cardFrom = new Card("1", LocalDate.now(), "1");
        Card cardTo = new Card("2", LocalDate.now(), "2");
        BigDecimal amount = BigDecimal.valueOf(1);
        Operation operation = new Operation(cardFrom, cardTo, amount);

        //when:
        repository.addOperation(operation);
        Optional<Operation> optOperation = repository.getOperation(operation.getOperationId());
        Optional<Operation> optNullOperation = repository.getOperation(UUID.randomUUID());

        //then:
        assertThat(operation, is(equalTo(optOperation.get())));
        assertThat(optNullOperation.isEmpty(), is(equalTo(true)));
    }

    @Test
    void addBalance() {
        //given:
        CardBalance cardBalance = new CardBalance(new Card("1", LocalDate.now(), "1"), BigDecimal.valueOf(10.00));

        //when:
        repository.addBalance(cardBalance, BigDecimal.valueOf(10));

        //then:
        assertThat(cardBalance.getBalance(), is(equalTo(BigDecimal.valueOf(20.00))));
    }

    @Test
    void subtractBalance() {
        //given:
        CardBalance cardBalance = new CardBalance(new Card("1", LocalDate.now(), "1"), BigDecimal.valueOf(20.00));

        //when:
        repository.subtractBalance(cardBalance, BigDecimal.valueOf(10));

        //then:
        assertThat(cardBalance.getBalance(), is(equalTo(BigDecimal.valueOf(10.00))));
    }
}