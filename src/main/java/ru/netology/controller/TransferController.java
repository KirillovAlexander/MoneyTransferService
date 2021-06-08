package ru.netology.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.netology.dto.ConfirmOperationDTO;
import ru.netology.dto.OperationDTO;
import ru.netology.dto.ResponseTransferDTO;
import ru.netology.exceptions.ErrorInputData;
import ru.netology.exceptions.ErrorTransfer;
import ru.netology.service.transfer.TransferService;

import java.util.UUID;

@CrossOrigin(maxAge = 3600)
@RestController("/")
public class TransferController {

    private final TransferService service;

    public TransferController(TransferService service) {
        this.service = service;
    }

    @PostMapping("transfer")
    public ResponseTransferDTO transfer(@RequestBody OperationDTO operationDTO) {
        UUID operationId = service.transfer(operationDTO);
        return new ResponseTransferDTO(operationId);
    }

    @PostMapping("confirmOperation")
    public ResponseTransferDTO confirmOperation(@RequestBody ConfirmOperationDTO confirmOperationDTO) {
        UUID operationId = service.confirmOperation(confirmOperationDTO.getOperationId(), confirmOperationDTO.getCode());
        return new ResponseTransferDTO(operationId);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ErrorInputData.class)
    ResponseTransferDTO invalidCredentials(ErrorInputData e) {
        return new ResponseTransferDTO(e.getLocalizedMessage(), 0);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ErrorTransfer.class)
    ResponseTransferDTO unauthorizedUser(ErrorTransfer e) {
        return new ResponseTransferDTO(e.getLocalizedMessage(), 0);
    }
}
