package ru.matyasov.app.accounting.controllers;

import jakarta.validation.Valid;
import org.assertj.core.util.Throwables;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.matyasov.app.accounting.dto.CurrencyDto;
import ru.matyasov.app.accounting.models.references.system.Currency;
import ru.matyasov.app.accounting.services.CurrenciesService;
import ru.matyasov.app.accounting.utils.*;

import java.util.List;
import java.util.stream.Collectors;

import static ru.matyasov.app.accounting.utils.NotDeletedException.notDeletedException;
import static ru.matyasov.app.accounting.utils.NotFoundException.notFoundException;
import static ru.matyasov.app.accounting.utils.NotUpdatedException.notUpdatedException;

@RestController
@RequestMapping("/currencies")
public class CurrenciesController {

    private final CurrenciesService currenciesService;

    private final ModelMapper modelMapper;

    @Autowired
    public CurrenciesController(CurrenciesService currenciesService, ModelMapper modelMapper) {
        this.currenciesService = currenciesService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<CurrencyDto> getAll() {
        return currenciesService.getAll().stream().map(this::convertToCurrencyDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CurrencyDto getById(@PathVariable("id") int id) {
        return convertToCurrencyDto(currenciesService.getById(id).orElseThrow(notFoundException("Запись с id = {0} не найдена.", id)));
    }

    @PostMapping
    public ResponseEntity<CurrencyDto> create(@RequestBody @Valid CurrencyDto currencyDto,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new NotCreatedException(ErrorMessage.getMessage(bindingResult));
        }

        Currency createdCurrency;

        try {
            createdCurrency = currenciesService.create(convertToCurrency(currencyDto));
        } catch (Throwable e) {
            throw new NotCreatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(convertToCurrencyDto(createdCurrency), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CurrencyDto> update(@PathVariable int id,
                                             @RequestBody @Valid CurrencyDto currencyDto,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new NotUpdatedException(ErrorMessage.getMessage(bindingResult));
        }

        currenciesService.getById(id).orElseThrow(notUpdatedException("Запись с id = {0} не найдена.", id));

        Currency updatedCurrency;

        try {
            updatedCurrency = currenciesService.update(id, convertToCurrency(currencyDto));
        } catch (Throwable e) {

            throw new NotUpdatedException(Throwables.getStackTrace(e));

        }

        return new ResponseEntity<>(convertToCurrencyDto(updatedCurrency), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable int id) {

        currenciesService.getById(id).orElseThrow(notDeletedException("Запись с id = {0} не найдена.", id));

        try {
            currenciesService.delete(id);
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

    private Currency convertToCurrency(CurrencyDto currencyDto) {
        return modelMapper.map(currencyDto, Currency.class);
    }

    private CurrencyDto convertToCurrencyDto(Currency currency) {
        return modelMapper.map(currency, CurrencyDto.class);
    }
}