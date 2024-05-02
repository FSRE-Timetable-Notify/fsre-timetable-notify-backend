package me.leopetrovic.fsretimetablenotify.common.dto;

import java.util.Optional;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
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
	@Schema(description = "HTTP status code of the error", example = "500")
	private HttpStatus status;

	@Schema(description = "Error code of the error", example = "Internal Server Error")
	private String error;

	@Schema(description = "An message which describes the error", example = "An error occurred while processing the request")
	private String message;

	@Schema(description = "Optional details of the error", example = "Optional details", nullable = true)
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