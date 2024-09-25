package me.leopetrovic.fsretimetablenotify.messaging.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "DTO to subscribe an email to a specific study program")
public record MessagingSubscribeDto(
		@Schema(description = "The email of the subscriber") String email,
		@Schema(description = "The FCM token of the subscriber") String fcmToken,
		@Schema(description = "The ID of the study program to subscribe to", requiredMode = RequiredMode.REQUIRED) Long studyProgramId) {

}
