package me.leopetrovic.fsretimetablenotify.messaging.models;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "messaging_subscriptions")
@Schema(description = "A subscription to receive messages")
@Data
public class MessagingSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    @Schema(
        description = "The ID of the subscription",
        requiredMode = RequiredMode.REQUIRED
    )
    private UUID id;

    @Column(name = "fcm_token", nullable = true)
    @Schema(description = "The FCM token of the subscription")
    private String fcmToken;

    @Column(name = "email", nullable = true)
    @Schema(description = "The email of the subscription")
    private String email;

    @Column(name = "study_program_id", nullable = false)
    @Schema(
        description = "The ID of the study program to subscribe to",
        requiredMode = RequiredMode.REQUIRED
    )
    private Long studyProgramId;

    public Optional<String> getFcmToken() {
        return Optional.ofNullable(fcmToken);
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }
}
