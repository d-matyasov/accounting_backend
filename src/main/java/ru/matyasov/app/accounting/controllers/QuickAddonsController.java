package ru.matyasov.app.accounting.controllers;

import jakarta.validation.Valid;
import org.assertj.core.util.Throwables;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.matyasov.app.accounting.dto.QuickAddonDto;
import ru.matyasov.app.accounting.models.references.user.QuickAddon;
import ru.matyasov.app.accounting.services.QuickAddonsService;
import ru.matyasov.app.accounting.utils.*;

import java.util.List;
import java.util.stream.Collectors;

import static ru.matyasov.app.accounting.utils.NotDeletedException.notDeletedException;
import static ru.matyasov.app.accounting.utils.NotFoundException.notFoundException;
import static ru.matyasov.app.accounting.utils.NotUpdatedException.notUpdatedException;

@RestController
@RequestMapping("/quick-addons")
public class QuickAddonsController {

    private final QuickAddonsService quickAddonsService;

    private final ModelMapper modelMapper;

    @Autowired
    public QuickAddonsController(QuickAddonsService quickAddonsService, ModelMapper modelMapper) {
        this.quickAddonsService = quickAddonsService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<QuickAddonDto> getAll() {
        return quickAddonsService.getAll().stream().map(this::convertToQuickAddonDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public QuickAddonDto getById(@PathVariable int id) {

        return convertToQuickAddonDto(quickAddonsService.getById(id).orElseThrow(notFoundException("Запись с id = {0} не найдена.", id)));

    }

    @PostMapping
    public ResponseEntity<QuickAddonDto> create(@RequestBody @Valid QuickAddonDto quickAddonDto,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new NotCreatedException(ErrorMessage.getMessage(bindingResult));
        }

        QuickAddon createdQuickAddon;

        try {
            createdQuickAddon = quickAddonsService.create(convertToQuickAddon(quickAddonDto));
        } catch (Throwable e) {
            throw new NotCreatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(convertToQuickAddonDto(createdQuickAddon), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<QuickAddonDto> update(@PathVariable int id,
                                             @RequestBody @Valid QuickAddonDto quickAddonDto,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new NotUpdatedException(ErrorMessage.getMessage(bindingResult));
        }

        quickAddonsService.getById(id).orElseThrow(notUpdatedException("Запись с id = {0} не найдена.", id));

        QuickAddon updatedQuickAddon;

        try {
            updatedQuickAddon = quickAddonsService.update(id, convertToQuickAddon(quickAddonDto));
        } catch (Throwable e) {
            throw new NotUpdatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(convertToQuickAddonDto(updatedQuickAddon), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {

        quickAddonsService.getById(id).orElseThrow(notDeletedException("Запись с id = {0} не найдена.", id));

        try {
            quickAddonsService.delete(id);
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

    protected QuickAddon convertToQuickAddon(QuickAddonDto quickAddonDto) {
        QuickAddon quickAddon = modelMapper.map(quickAddonDto, QuickAddon.class);
        return quickAddon;
    }

    protected QuickAddonDto convertToQuickAddonDto(QuickAddon quickAddon) {
        return modelMapper.map(quickAddon, QuickAddonDto.class);
    }

}