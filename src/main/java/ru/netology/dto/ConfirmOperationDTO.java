package ru.netology.dto;

public class ConfirmOperationDTO {
    private String operationId;
    private String code;

    public ConfirmOperationDTO(String operationId, String code) {
        this.operationId = operationId;
        this.code = code;
    }

    public String getOperationId() {
        return operationId;
    }

    public String getCode() {
        return code;
    }
}
