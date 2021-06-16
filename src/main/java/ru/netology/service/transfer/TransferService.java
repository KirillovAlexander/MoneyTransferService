package ru.netology.service.transfer;

import ru.netology.entity.operation.Operation;

import java.util.UUID;

public interface TransferService {
    public UUID transfer(Operation operation);

    public UUID confirmOperation(String operationId, String code);
}
