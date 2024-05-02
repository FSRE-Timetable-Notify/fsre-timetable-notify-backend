package me.leopetrovic.fsretimetablenotify.messaging.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO to subscribe an FCM token received to a specific study program")
public record MessagingSubscribeDto(@Schema(description = "The FCM token of the subscriber") String fcmToken,
		@Schema(description = "The ID of the study program to subscribe to") Long studyProgramId) {

}
