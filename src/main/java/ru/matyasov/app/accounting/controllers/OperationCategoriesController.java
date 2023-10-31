package ru.matyasov.app.accounting.controllers;

import jakarta.validation.Valid;
import org.assertj.core.util.Throwables;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.matyasov.app.accounting.dto.OperationCategoryDto;
import ru.matyasov.app.accounting.dto.QuickAddonDto;
import ru.matyasov.app.accounting.models.references.user.OperationCategory;
import ru.matyasov.app.accounting.models.references.user.QuickAddon;
import ru.matyasov.app.accounting.services.OperationCategoriesService;
import ru.matyasov.app.accounting.utils.*;

import java.util.List;
import java.util.stream.Collectors;

import static ru.matyasov.app.accounting.utils.NotDeletedException.notDeletedException;
import static ru.matyasov.app.accounting.utils.NotFoundException.notFoundException;
import static ru.matyasov.app.accounting.utils.NotUpdatedException.notUpdatedException;

@RestController
@RequestMapping("/operation-categories")
public class OperationCategoriesController {

    private final OperationCategoriesService operationCategoriesService;

    private final QuickAddonsController quickAddonsController;

    private final ModelMapper modelMapper;

    @Autowired
    public OperationCategoriesController(OperationCategoriesService operationCategoriesService, QuickAddonsController quickAddonsController, ModelMapper modelMapper) {
        this.operationCategoriesService = operationCategoriesService;
        this.quickAddonsController = quickAddonsController;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<OperationCategoryDto> getAll() {
        return operationCategoriesService.getAll().stream().map(this::convertToOperationCategoryDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public OperationCategoryDto getById(@PathVariable int id) {

        return convertToOperationCategoryDto(operationCategoriesService.getById(id).orElseThrow(notFoundException("Запись с id = {0} не найдена.", id)));

    }

    @PostMapping
    public ResponseEntity<OperationCategoryDto> create(@RequestBody @Valid OperationCategoryDto operationCategoryDto,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new NotCreatedException(ErrorMessage.getMessage(bindingResult));
        }

        OperationCategory createdOperationCategory;

        try {
            createdOperationCategory = operationCategoriesService.create(convertToOperationCategory(operationCategoryDto));
        } catch (Throwable e) {
            throw new NotCreatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(convertToOperationCategoryDto(createdOperationCategory), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OperationCategoryDto> update(@PathVariable int id,
                                             @RequestBody @Valid OperationCategoryDto operationCategoryDto,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new NotUpdatedException(ErrorMessage.getMessage(bindingResult));
        }

        operationCategoriesService.getById(id).orElseThrow(notUpdatedException("Запись с id = {0} не найдена.", id));

        OperationCategory updatedOperationCategory;

        try {
            updatedOperationCategory = operationCategoriesService.update(id, convertToOperationCategory(operationCategoryDto));
        } catch (Throwable e) {
            throw new NotUpdatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(convertToOperationCategoryDto(updatedOperationCategory), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {

        operationCategoriesService.getById(id).orElseThrow(notDeletedException("Запись с id = {0} не найдена.", id));

        try {
            operationCategoriesService.delete(id);
        } catch (Throwable e) {
            throw new NotDeletedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping("/{id}/quick-addons")
    public List<QuickAddonDto> getQuickAddonList(@PathVariable int id) {

        return operationCategoriesService.getQuickAddonList(id).stream().map(quickAddonsController::convertToQuickAddonDto).toList();

    }

    @PostMapping("/{id}/quick-addons")
    public ResponseEntity<List<QuickAddonDto>> addQuickAddonList(@PathVariable int id,
                                                 @RequestBody List<QuickAddonDto> addedQuickAddonDtoList) {

        OperationCategory operationCategory = operationCategoriesService.getById(id).orElseThrow(notFoundException("Запись с id = {0} не найдена.", id));

        List<QuickAddon> quickAddonList;

        try {
            quickAddonList = operationCategoriesService.addQuickAddonList(operationCategory, addedQuickAddonDtoList.stream().map(quickAddonsController::convertToQuickAddon).toList());
        } catch (Throwable e) {
            throw new NotCreatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(quickAddonList.stream().map(quickAddonsController::convertToQuickAddonDto).toList(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/quick-addons")

    public ResponseEntity<List<QuickAddonDto>> deleteQuickAddonList(@PathVariable int id,
                                                                    @RequestParam(value = "ids", required = false) String ids) {

        if (ids == null) {
            throw new WrongParametersException("Не указан параметр - список ИД: ids=");
        }

        if (!ids.matches("^\\d+(,\\d+)?")) {
            throw new WrongParametersException("Не правильные параметры: ids=" + ids);
        }

        OperationCategory operationCategory = operationCategoriesService.getById(id).orElseThrow(notFoundException("Запись с id = {0} не найдена.", id));

        List<QuickAddon> quickAddonList;

        try {
            quickAddonList = operationCategoriesService.deleteQuickAddonList(operationCategory, ids);
        } catch (Throwable e) {
            throw new NotCreatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(quickAddonList.stream().map(quickAddonsController::convertToQuickAddonDto).toList(), HttpStatus.OK);
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
    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(WrongParametersException e) {
        return new ResponseEntity<>(ErrorResponse.getResponse(e), HttpStatus.BAD_REQUEST);
    }

    private OperationCategory convertToOperationCategory(OperationCategoryDto operationCategoryDto) {
        return modelMapper.map(operationCategoryDto, OperationCategory.class);
    }

    private OperationCategoryDto convertToOperationCategoryDto(OperationCategory operationCategory) {
        return modelMapper.map(operationCategory, OperationCategoryDto.class);
    }

}