package me.leopetrovic.fsretimetablenotify.messaging.exceptions;

public class FcmSendMessageFailed extends RuntimeException {
	public FcmSendMessageFailed(String message) {
		super("Failed to send message using FCM: " + message);
	}

	public FcmSendMessageFailed(String message, Throwable cause) {
		super("Failed to send message using FCM: " + message, cause);
	}
}
