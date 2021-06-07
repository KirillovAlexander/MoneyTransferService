package ru.netology.exceptions;

public class ErrorInputData extends RuntimeException{
    public ErrorInputData() {
        super("Неверные данные.");
    }
}
