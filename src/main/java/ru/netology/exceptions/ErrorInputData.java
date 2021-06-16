package ru.netology.exceptions;

public class ErrorInputData extends RuntimeException{
    public ErrorInputData(String message) {
        super(message);
    }
}
