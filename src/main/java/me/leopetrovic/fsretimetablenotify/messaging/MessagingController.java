package me.leopetrovic.fsretimetablenotify.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import me.leopetrovic.fsretimetablenotify.messaging.dto.MessagingSubscribeDto;
import me.leopetrovic.fsretimetablenotify.messaging.models.MessagingSubscription;

@RestController
@RequestMapping("/messaging")
@Validated
public class MessagingController {
	@Autowired
	private MessagingService messagingService;

	@PostMapping("/subscribe")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Subscribe to a study program using an email and/or FCM token")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully subscribed to a topic", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = MessagingSubscription.class))
			})
	})
	public ResponseEntity<MessagingSubscription> subscribe(
			@RequestBody @Valid MessagingSubscribeDto messagingSubscribeDto) {
		return ResponseEntity
				.ok(messagingService.subscribe(messagingSubscribeDto.email(), messagingSubscribeDto.fcmToken(),
						messagingSubscribeDto.studyProgramId()));
	}

	@PostMapping("/unsubscribe")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Unsubscribe from a study program for a specific email and/or FCM token")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully unsubscribed from a topic")
	})
	public ResponseEntity<Void> unsubscribe(
			@RequestBody @Valid MessagingSubscribeDto messagingSubscribeDto) {
		messagingService.unsubscribe(messagingSubscribeDto.email(), messagingSubscribeDto.fcmToken(),
				messagingSubscribeDto.studyProgramId());

		return ResponseEntity.ok().build();
	}
}
