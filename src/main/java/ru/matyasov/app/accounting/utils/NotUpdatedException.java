package ru.matyasov.app.accounting.utils;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class NotUpdatedException extends RuntimeException {


    public NotUpdatedException(String message) {
        super(message);
    }

    public NotUpdatedException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public static Supplier<NotUpdatedException> notUpdatedException(String message) {
        return () -> new NotUpdatedException(message);
    }
    public static Supplier<NotUpdatedException> notUpdatedException(String message, Object... args) {
        return () -> new NotUpdatedException(message, args);
    }
}
