package ru.netology.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseTransferDTO {

    private UUID operationId;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int id;

    public ResponseTransferDTO(String message, int id) {
        this.message = message;
        this.id = id;
    }

    public ResponseTransferDTO(UUID operationId) {
        this.operationId = operationId;
    }

    public UUID getOperationId() {
        return operationId;
    }

    public void setOperationId(UUID operationId) {
        this.operationId = operationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
