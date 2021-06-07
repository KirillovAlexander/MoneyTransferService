package ru.netology.exceptions;

public class ErrorTransfer extends RuntimeException{
    public ErrorTransfer(String message) {
        super(message);
    }
}
