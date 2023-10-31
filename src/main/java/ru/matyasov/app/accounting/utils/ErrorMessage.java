package ru.matyasov.app.accounting.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class ErrorMessage {

    private String message;

    public static String getMessage(BindingResult bindingResult) {

        StringBuilder errorMsg = new StringBuilder();

        List<FieldError> errors = bindingResult.getFieldErrors();

        for (FieldError error : errors) {
            errorMsg.append(errorMsg.length() == 0 ? "" : "\n")
                    .append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage())
                    .append(";");
        }
        return errorMsg.toString();
    }

    public static String getMessage(String msg) {
        return msg;
    }

}
