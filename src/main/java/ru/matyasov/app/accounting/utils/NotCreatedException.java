package ru.matyasov.app.accounting.utils;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class NotCreatedException extends RuntimeException {

    public NotCreatedException(String message) {
        super(message);
    }

    public NotCreatedException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public static Supplier<NotCreatedException> notCreatedException(String message) {
        return () -> new NotCreatedException(message);
    }
    public static Supplier<NotCreatedException> notCreatedException(String message, Object... args) {
        return () -> new NotCreatedException(message, args);
    }
}
