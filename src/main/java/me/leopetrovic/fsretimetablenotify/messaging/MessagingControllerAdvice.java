package me.leopetrovic.fsretimetablenotify.messaging;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import me.leopetrovic.fsretimetablenotify.common.dto.FsreError;
import me.leopetrovic.fsretimetablenotify.messaging.exceptions.MessagingSubscriptionAlreadyRegisteredException;

@Order(1)
@ControllerAdvice(assignableTypes = MessagingController.class)
public class MessagingControllerAdvice {
	@ExceptionHandler(MessagingSubscriptionAlreadyRegisteredException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "409", description = "Messaging Subscription Already Registered", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = FsreError.class))
			})
	})
	public ResponseEntity<FsreError> handleMessagingSubscriptionAlreadyRegisteredException(
			MessagingSubscriptionAlreadyRegisteredException e) {
		final FsreError fsreException = new FsreError(HttpStatus.CONFLICT,
				"Messaging Subscription Already Registered",
				e.getMessage());

		return ResponseEntity.status(fsreException.getStatus()).body(fsreException);
	}
}