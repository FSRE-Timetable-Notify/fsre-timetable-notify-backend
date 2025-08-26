package me.leopetrovic.fsretimetablenotify.timetable;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import me.leopetrovic.fsretimetablenotify.common.dto.FsreError;
import me.leopetrovic.fsretimetablenotify.timetable.exceptions.TimetableFetchException;
import me.leopetrovic.fsretimetablenotify.timetable.exceptions.TimetableParseException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.format.DateTimeParseException;

@Order(1)
@ControllerAdvice(assignableTypes = TimetableController.class)
public class TimetableControllerAdvice {
    @ExceptionHandler(
        {TimetableFetchException.class, TimetableParseException.class}
    )
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ApiResponses(
        value = {@ApiResponse(
            responseCode = "502",
            description = "Bad Gateway",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FsreError.class)
            )}
        )}
    )
    public ResponseEntity<FsreError> handleTimetableException(Exception e) {
        final FsreError fsreException = new FsreError(HttpStatus.BAD_GATEWAY,
            "Bad Gateway",
            "Failed to make request to upstream server",
            e.getMessage());

        return ResponseEntity.status(fsreException.getStatus())
            .body(fsreException);
    }

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponses(
        value = {@ApiResponse(
            responseCode = "400",
            description = "Failed to parse ISO week",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FsreError.class)
            )}
        )}
    )
    public ResponseEntity<FsreError> handleDateTimeParseException(
        DateTimeParseException e
    ) {
        final FsreError fsreException = new FsreError(HttpStatus.BAD_REQUEST,
            "Bad Request",
            "Failed to parse ISO week",
            e.getMessage());

        return ResponseEntity.status(fsreException.getStatus())
            .body(fsreException);
    }
}