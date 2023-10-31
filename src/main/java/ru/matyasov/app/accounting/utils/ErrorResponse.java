package ru.matyasov.app.accounting.utils;

public class ErrorResponse {

    private String message;

    private long timestamp;

    private ErrorResponse response;

    public ErrorResponse(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static ErrorResponse getResponse(RuntimeException e) {

        return new ErrorResponse(e.getMessage(), System.currentTimeMillis());

    }

}
