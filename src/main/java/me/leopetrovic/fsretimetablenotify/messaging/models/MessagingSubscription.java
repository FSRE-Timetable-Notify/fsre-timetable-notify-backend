package me.leopetrovic.fsretimetablenotify.messaging.models;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "messaging_subscriptions")
@Schema(description = "A subscription to receive messages")
@Data
public class MessagingSubscription {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false, nullable = false, unique = true)
	@Schema(description = "The ID of the subscription")
	private UUID id;

	@Column(name = "fcm_token", nullable = false)
	@Schema(description = "The FCM token of the subscription")
	private String fcmToken;

	@Column(name = "study_program_id", nullable = false)
	@Schema(description = "The ID of the study program to subscribe to")
	private Long studyProgramId;
}
