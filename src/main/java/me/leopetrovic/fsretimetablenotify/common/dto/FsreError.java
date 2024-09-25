package me.leopetrovic.fsretimetablenotify.common.dto;

import java.util.Optional;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Schema(description = "An error thrown by the FSRE API")
public class FsreError {
	@Schema(description = "HTTP status code of the error", example = "500", requiredMode = RequiredMode.REQUIRED)
	private HttpStatus status;

	@Schema(description = "Error code of the error", example = "Internal Server Error", requiredMode = RequiredMode.REQUIRED)
	private String error;

	@Schema(description = "An message which describes the error", example = "An error occurred while processing the request", requiredMode = RequiredMode.REQUIRED)
	private String message;

	@Schema(description = "Optional details of the error", example = "The request failed because the network is unreachable")
	private Optional<String> details;

	public FsreError(HttpStatus status, String error, String message) {
		this.status = status;
		this.error = error;
		this.message = message;
		this.details = Optional.empty();
	}

	public FsreError(HttpStatus status, String error, String message, String details) {
		this.status = status;
		this.error = error;
		this.message = message;
		this.details = Optional.of(details);
	}
}