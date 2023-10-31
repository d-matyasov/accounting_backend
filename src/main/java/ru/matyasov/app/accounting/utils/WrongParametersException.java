package ru.matyasov.app.accounting.utils;

public class WrongParametersException extends RuntimeException {

    public WrongParametersException(String message) {
        super(message);
    }
}
