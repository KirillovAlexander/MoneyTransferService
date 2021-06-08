package ru.netology.cardtransfer.dto;

public class ConfirmTestDTO {
    private String operationId;
    private String code;

    public ConfirmTestDTO(String operationId, String code) {
        this.operationId = operationId;
        this.code = code;
    }
}
