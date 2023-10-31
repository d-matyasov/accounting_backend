package ru.matyasov.app.accounting.controllers;


import jakarta.validation.Valid;
import org.assertj.core.util.Throwables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.matyasov.app.accounting.models.references.system.AccountingObjectType;
import ru.matyasov.app.accounting.services.AccountingObjectTypesService;
import ru.matyasov.app.accounting.utils.*;

import java.util.List;

import static ru.matyasov.app.accounting.utils.NotDeletedException.notDeletedException;
import static ru.matyasov.app.accounting.utils.NotFoundException.notFoundException;
import static ru.matyasov.app.accounting.utils.NotUpdatedException.notUpdatedException;

@RestController
@RequestMapping("/accounting-object-types")
public class AccountingObjectTypesController {

    private final AccountingObjectTypesService accountingObjectTypesService;

    @Autowired
    public AccountingObjectTypesController(AccountingObjectTypesService accountingObjectTypesService) {
        this.accountingObjectTypesService = accountingObjectTypesService;
    }

    @GetMapping
    public List<AccountingObjectType> getAll() {
        return accountingObjectTypesService.getAll();
    }

    @GetMapping("/{id}")
    public AccountingObjectType getById(@PathVariable int id) {

        return accountingObjectTypesService.getById(id).orElseThrow(notFoundException("Запись с id = {0} не найдена.", id));

    }

    @PostMapping
    public ResponseEntity<AccountingObjectType> create(@RequestBody @Valid AccountingObjectType accountingObjectType,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new NotCreatedException(ErrorMessage.getMessage(bindingResult));
        }

        AccountingObjectType createdAccountingObjectType;

        try {
            createdAccountingObjectType = accountingObjectTypesService.create(accountingObjectType);
        } catch (Throwable e) {
            throw new NotCreatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(createdAccountingObjectType, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountingObjectType> update(@PathVariable int id,
                                             @RequestBody @Valid AccountingObjectType accountingObjectType,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new NotUpdatedException(ErrorMessage.getMessage(bindingResult));
        }

        accountingObjectTypesService.getById(id).orElseThrow(notUpdatedException("Запись с id = {0} не найдена.", id));

        AccountingObjectType updatedAccountingObjectType;

        try {
            updatedAccountingObjectType = accountingObjectTypesService.update(id, accountingObjectType);
        } catch (Throwable e) {
            throw new NotUpdatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(updatedAccountingObjectType, HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {

        accountingObjectTypesService.getById(id).orElseThrow(notDeletedException("Запись с id = {0} не найдена.", id));

        try {
            accountingObjectTypesService.delete(id);
        } catch (Throwable e) {
            throw new NotDeletedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(HttpStatus.OK);

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

}
