package ru.matyasov.app.accounting.controllers;

import jakarta.validation.Valid;
import org.assertj.core.util.Throwables;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.matyasov.app.accounting.dto.UserDto;
import ru.matyasov.app.accounting.models.references.system.User;
import ru.matyasov.app.accounting.models.references.system.UserRole;
import ru.matyasov.app.accounting.services.UsersService;
import ru.matyasov.app.accounting.utils.*;

import java.util.List;
import java.util.stream.Collectors;

import static ru.matyasov.app.accounting.utils.NotDeletedException.notDeletedException;
import static ru.matyasov.app.accounting.utils.NotFoundException.notFoundException;
import static ru.matyasov.app.accounting.utils.NotUpdatedException.notUpdatedException;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    private final ModelMapper modelMapper;

    @Autowired
    public UsersController(UsersService usersService, ModelMapper modelMapper) {
        this.usersService = usersService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<UserDto> getAll() {
        return usersService.getAll().stream().map(this::convertToUserDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable int id) {

        System.out.println(usersService.getById(id).orElseThrow(notFoundException("Запись с id = {0} не найдена.", id)).toString());

        return convertToUserDto(usersService.getById(id).orElseThrow(notFoundException("Запись с id = {0} не найдена.", id)));

    }

    @PatchMapping("/{id}/change-is-enabled")
    public ResponseEntity<UserDto> update(@PathVariable int id,
                                          @RequestBody @Valid UserDto userDto,
                                          BindingResult bindingResult) {

        User userToUpdate = usersService.getById(id).orElseThrow(notUpdatedException("Запись с id = {0} не найдена.", id));

        userToUpdate.setIsEnabled(userDto.getIsEnabled());

        User updatedUser;

        try {
            updatedUser = usersService.update(id, userToUpdate);
        } catch (Throwable e) {
            throw new NotUpdatedException(Throwables.getStackTrace(e));
        }

        return new ResponseEntity<>(convertToUserDto(updatedUser), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {

        usersService.getById(id).orElseThrow(notDeletedException("Запись с id = {0} не найдена.", id));

        try {
            usersService.delete(id);
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

    private UserDto convertToUserDto(User user) {

        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setLogin(user.getUsername());
        userDto.setIsEnabled(user.isEnabled());
        userDto.setRole(UserRole.valueOf(user.getAuthorities().stream().findFirst().orElseThrow(notFoundException("У пользователя отсутствует роль!")).toString()));

        return userDto;
    }

}