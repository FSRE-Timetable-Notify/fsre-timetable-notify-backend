package me.leopetrovic.fsretimetablenotify.messaging.exceptions;

public class MessagingSubscriptionAlreadyRegisteredException extends RuntimeException {
	public MessagingSubscriptionAlreadyRegisteredException(String fcmToken, Long studyProgramId) {
		super("Messaging subscription with FCM token " + fcmToken + " was already registered for study program with ID "
				+ studyProgramId);
	}

	public MessagingSubscriptionAlreadyRegisteredException(String fcmToken, Long studyProgramId, Throwable cause) {
		super("Messaging subscription with FCM token " + fcmToken + " was already registered for study program with ID "
				+ studyProgramId, cause);
	}
}
