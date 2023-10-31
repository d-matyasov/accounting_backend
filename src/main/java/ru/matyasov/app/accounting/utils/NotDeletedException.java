package ru.matyasov.app.accounting.utils;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class NotDeletedException extends RuntimeException{


    public NotDeletedException(String message) {
        super(message);
    }

    public NotDeletedException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public static Supplier<NotDeletedException> notDeletedException(String message) {
        return () -> new NotDeletedException(message);
    }
    public static Supplier<NotDeletedException> notDeletedException(String message, Object... args) {
        return () -> new NotDeletedException(message, args);
    }
}
