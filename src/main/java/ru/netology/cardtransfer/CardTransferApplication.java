package ru.netology.cardtransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.netology")
public class CardTransferApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardTransferApplication.class, args);
    }

}
