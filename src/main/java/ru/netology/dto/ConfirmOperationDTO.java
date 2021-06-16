package ru.netology.dto;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
public class ConfirmOperationDTO {
    @NotBlank
    private String operationId;
    @NotBlank
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
