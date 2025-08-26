package me.leopetrovic.fsretimetablenotify.messaging.exceptions;

public class MessagingSubscriptionAlreadyRegisteredException extends
    RuntimeException {
    public MessagingSubscriptionAlreadyRegisteredException(
        String email,
        String fcmToken,
        Long studyProgramId
    ) {
        super("Messaging subscription with credentails: (" + email + ", " + fcmToken + ") was already registered for study program with ID " + studyProgramId);
    }

    public MessagingSubscriptionAlreadyRegisteredException(
        String email,
        String fcmToken,
        Long studyProgramId,
        Throwable cause
    ) {
        super("Messaging subscription with credentails: (" + email + ", " + fcmToken + ") was already registered for study program with ID " + studyProgramId,
            cause);
    }
}
