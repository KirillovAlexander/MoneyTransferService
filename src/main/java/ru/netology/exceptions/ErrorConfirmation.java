package ru.netology.exceptions;

public class ErrorConfirmation extends RuntimeException{
    public ErrorConfirmation(String message) {
        super(message);
    }
}
