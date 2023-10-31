package ru.matyasov.app.accounting.controllers;

import jakarta.validation.Valid;
import org.assertj.core.util.Throwables;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.matyasov.app.accounting.dto.AccountingObjectDto;
import ru.matyasov.app.accounting.models.references.user.AccountingObject;
import ru.matyasov.app.accounting.models.views.AccountingObjectOrTransfer;
import ru.matyasov.app.accounting.services.AccountingObjectsOrTransfersService;
import ru.matyasov.app.accounting.services.AccountingObjectsService;
import ru.matyasov.app.accounting.utils.*;

import java.util.List;
import java.util.stream.Collectors;

import static ru.matyasov.app.accounting.utils.NotDeletedException.notDeletedException;
import static ru.matyasov.app.accounting.utils.NotFoundException.notFoundException;
import static ru.matyasov.app.accounting.utils.NotUpdatedException.notUpdatedException;

@RestController
@RequestMapping("/accounting-objects")
public class AccountingObjectsController {

    private final AccountingObjectsService accountingObjectsService;

    private final AccountingObjectsOrTransfersService accountingObjectsOrTransfersService;

    private final ModelMapper modelMapper;

    @Autowired
    public AccountingObjectsController(AccountingObjectsService accountingObjectsService, AccountingObjectsOrTransfersService accountingObjectsOrTransfersService, ModelMapper modelMapper) {
        this.accountingObjectsService = accountingObjectsService;
        this.accountingObjectsOrTransfersService = accountingObjectsOrTransfersService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<AccountingObjectDto> getAll() {
        return accountingObjectsService.getAll().stream().map(this::convertToAccountingObjectDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AccountingObjectDto getById(@PathVariable int id) {

        System.out.println(accountingObjectsService.getById(id).orElseThrow(notFoundException("Запись с id = {0} не найдена.", id)).toString());

        return convertToAccountingObjectDto(accountingObjectsService.getById(id).orElseThrow(notFoundException("Запись с id = {0} не найдена.", id)));

    }

    @PostMapping
    public ResponseEntity<AccountingObjectDto> create(@RequestBody @Valid AccountingObjectDto accountingObjectDto,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new NotCreatedException(ErrorMessage.getMessage(bindingResult));
        }

        AccountingObject createdAccountingObject;

        try {
            createdAccountingObject = accountingObjectsService.create(convertToAccountingObject(accountingObjectDto));
        } catch (Throwable e) {
            throw new NotCreatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(convertToAccountingObjectDto(createdAccountingObject), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountingObjectDto> update(@PathVariable int id,
                                             @RequestBody @Valid AccountingObjectDto accountingObjectDto,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new NotUpdatedException(ErrorMessage.getMessage(bindingResult));
        }

        accountingObjectsService.getById(id).orElseThrow(notUpdatedException("Запись с id = {0} не найдена.", id));

        AccountingObject updatedAccountingObject;

        try {
            updatedAccountingObject = accountingObjectsService.update(id, convertToAccountingObject(accountingObjectDto));
        } catch (Throwable e) {
            throw new NotUpdatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(convertToAccountingObjectDto(updatedAccountingObject), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {

        accountingObjectsService.getById(id).orElseThrow(notDeletedException("Запись с id = {0} не найдена.", id));

        try {
            accountingObjectsService.delete(id);
        } catch (Throwable e) {
            throw new NotDeletedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping("/accounting-objects-and-transfers-view")
    public List<AccountingObjectOrTransfer> getAllAccountingObjectsAndTransfers() {
        return accountingObjectsOrTransfersService.getAll();
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

    private AccountingObject convertToAccountingObject(AccountingObjectDto accountingObjectDto) {
        return modelMapper.map(accountingObjectDto, AccountingObject.class);
    }

    private AccountingObjectDto convertToAccountingObjectDto(AccountingObject accountingObject) {
        return modelMapper.map(accountingObject, AccountingObjectDto.class);
    }

}