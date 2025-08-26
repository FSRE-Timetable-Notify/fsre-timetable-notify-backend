package me.leopetrovic.fsretimetablenotify.messaging.exceptions;

public class EmailSendMessageFailed extends RuntimeException {
    public EmailSendMessageFailed(String message) {
        super("Failed to send message using Email: " + message);
    }

    public EmailSendMessageFailed(String message, Throwable cause) {
        super("Failed to send message using Email: " + message, cause);
    }
}
