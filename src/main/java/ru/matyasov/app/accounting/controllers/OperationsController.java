package ru.matyasov.app.accounting.controllers;

import jakarta.validation.Valid;
import org.assertj.core.util.Throwables;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import ru.matyasov.app.accounting.dto.OperationDto;
import ru.matyasov.app.accounting.models.Operation;
import ru.matyasov.app.accounting.models.references.user.AccountingObject;
import ru.matyasov.app.accounting.models.references.user.OperationCategory;
import ru.matyasov.app.accounting.models.views.AccountingObjectOrTransfer;
import ru.matyasov.app.accounting.services.AccountingObjectsOrTransfersService;
import ru.matyasov.app.accounting.services.AccountingObjectsService;
import ru.matyasov.app.accounting.services.OperationsService;
import ru.matyasov.app.accounting.utils.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ru.matyasov.app.accounting.utils.NotCreatedException.notCreatedException;
import static ru.matyasov.app.accounting.utils.NotDeletedException.notDeletedException;
import static ru.matyasov.app.accounting.utils.NotUpdatedException.notUpdatedException;

@RestController
@RequestMapping("/operations")
public class OperationsController {

    private final OperationsService operationsService;

    private final AccountingObjectsOrTransfersService accountingObjectsOrTransfersService;

    private final AccountingObjectsService accountingObjectsService;

    private final ModelMapper modelMapper;


    @Autowired
    public OperationsController(OperationsService operationsService, AccountingObjectsOrTransfersService accountingObjectsOrTransfersService, AccountingObjectsService accountingObjectsService, ModelMapper modelMapper) {
        this.operationsService = operationsService;
        this.accountingObjectsOrTransfersService = accountingObjectsOrTransfersService;
        this.accountingObjectsService = accountingObjectsService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid OperationDto operationDto,
                                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new NotCreatedException(ErrorMessage.getMessage(bindingResult));
        }

        Operation createdOperation;

        try {
            createdOperation = operationsService.create(convertToOperation(operationDto));
        } catch (Throwable e) {
            throw new NotCreatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(operationsService.getOperationAsJsonById(createdOperation.getId()), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable int id,
                                             @RequestBody @Valid OperationDto operationDto,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new NotUpdatedException(ErrorMessage.getMessage(bindingResult));
        }

        operationsService.getById(id).orElseThrow(notUpdatedException("Запись с id = {0} не найдена.", id));

        Operation updatedOperation;

        try {
            updatedOperation = operationsService.update(id, convertToOperation(operationDto));
        } catch (Throwable e) {
            throw new NotUpdatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(operationsService.getOperationAsJsonById(updatedOperation.getId()), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {

        operationsService.getById(id).orElseThrow(notDeletedException("Запись с id = {0} не найдена.", id));

        try {
            operationsService.delete(id);
        } catch (Throwable e) {
            throw new NotDeletedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PatchMapping("/{id}/shift-up")
    public ResponseEntity<String> shiftUp(@PathVariable int id) {

        operationsService.getById(id).orElseThrow(notUpdatedException("Запись с id = {0} не найдена.", id));

        try {
            operationsService.shiftUp(id);
        } catch (Throwable e) {
            throw new NotUpdatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(operationsService.getOperationAsJsonById(id), HttpStatus.OK);

    }

    @PatchMapping("/{id}/shift-down")
    public ResponseEntity<String> shiftDown(@PathVariable int id) {

        operationsService.getById(id).orElseThrow(notUpdatedException("Запись с id = {0} не найдена.", id));

        try {
            operationsService.shiftDown(id);
        } catch (Throwable e) {
            throw new NotUpdatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(operationsService.getOperationAsJsonById(id), HttpStatus.OK);

    }

    @PatchMapping("/{id}/change-is-fact")
    public ResponseEntity<String> changeIsFact(@PathVariable int id,
                                               @RequestBody OperationDto operationDto,
                                               BindingResult bindingResult) {

        operationsService.getById(id).orElseThrow(notUpdatedException("Запись с id = {0} не найдена.", id));

        try {
            operationsService.changeIsFact(id, operationDto.getIsFact());
        } catch (Throwable e) {
            throw new NotUpdatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(operationsService.getOperationAsJsonById(id), HttpStatus.OK);

    }
    @GetMapping("/headers-and-data")
    public Object getOperationsTableJson(
                @RequestParam(value = "dateFrom")
                @DateTimeFormat(pattern="yyyy-MM-dd")
                Date dateFrom,
                @RequestParam(value = "dateTo", required = false)
                @DateTimeFormat(pattern="yyyy-MM-dd")
                Date dateTo) {

        if (operationsService.getOperationsTableJson(dateFrom, dateTo) == null) {
            throw new NotFoundException("Данные отсутствуют.");
        }

        return operationsService.getOperationsTableJson(dateFrom, dateTo);
    }

    @GetMapping("/facts-row")
    public ResponseEntity<String> getFactsRow() {

        String facts;

        try {
            facts = operationsService.getFactsRowAsJson();
        } catch (Throwable e) {
            throw new NotUpdatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(facts, HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(NotFoundException e) {

        return new ResponseEntity<>(ErrorResponse.getResponse(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(NotCreatedException e) {
        return new ResponseEntity<>(ErrorResponse.getResponse(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(NotUpdatedException e) {
        return new ResponseEntity<>(ErrorResponse.getResponse(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(NotDeletedException e) {
        return new ResponseEntity<>(ErrorResponse.getResponse(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException e) {

        final Map<String, String> result = new HashMap<>();

        result.put("message", e.getMessage());

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    private Operation convertToOperation(OperationDto operationDto) {

        Operation operation = new Operation();

        AccountingObjectOrTransfer accountingObjectOrTransfer = accountingObjectsOrTransfersService.getById(operationDto.getAccountingObjectOrTransferId()).orElseThrow(notCreatedException("accountingObjectsOrTransfersService / Запись с id = {0} не найдена.", operationDto.getAccountingObjectOrTransferId()));

        if (operationDto.getId() != null) {

            operation.setId(operationDto.getId());

        }

        operation.setDate(operationDto.getDate());

        operation.setIndexByDate(operationDto.getIndexByDate());

        if (!operationDto.getAccountingObjectOrTransferId().contains(",")) {

            operation.setAmountPlus(operationDto.getAmountPlus());

        }

        operation.setAmountMinus(operationDto.getAmountMinus());

        operation.setIsFact(operationDto.getIsFact());

        OperationCategory category;

        if (operationDto.getCategoryId() != null) {

            category = new OperationCategory();

            category.setId(operationDto.getCategoryId());

            operation.setCategory(category);
        }

        operation.setCategoryAddon(operationDto.getCategoryAddon());

        AccountingObject accountingObject = accountingObjectsService.getById(accountingObjectOrTransfer.getAccountingObjectId()).orElseThrow(notCreatedException("accountingObjectsService.getById / Запись с id = {0} не найдена.", accountingObjectOrTransfer.getAccountingObjectId()));

        operation.setAccountingObject(accountingObject);

        operation.setComment(operationDto.getComment());


        Operation pairOperation = new Operation();

        if (operationDto.getAccountingObjectOrTransferId().contains(",")) {

            if (operationDto.getPairOperationId() != null) {

                pairOperation.setId(operationDto.getPairOperationId());

            }

            pairOperation.setDate(operation.getDate());

            pairOperation.setIndexByDate(operation.getIndexByDate() + 1);

            pairOperation.setAmountPlus(operationDto.getAmountPlus());

            pairOperation.setIsFact(operation.getIsFact());

            pairOperation.setCategory(operation.getCategory());

            pairOperation.setCategoryAddon(operation.getCategoryAddon());


            AccountingObject pairAccountingObject = accountingObjectsService.getById(accountingObjectOrTransfer.getPairAccountingObjectId()).orElseThrow(notCreatedException("accountingObjectsService.getById / Запись с id = {0} не найдена.", accountingObjectOrTransfer.getPairAccountingObjectId()));

            pairOperation.setAccountingObject(pairAccountingObject);

            pairOperation.setComment(operation.getComment());

            operation.setPairOperation(pairOperation);

            pairOperation.setPairOperation(operation);

        }

        return operation;
    }

    private OperationDto convertToOperationDto(Operation operation) {
        return modelMapper.map(operation, OperationDto.class);
    }

}