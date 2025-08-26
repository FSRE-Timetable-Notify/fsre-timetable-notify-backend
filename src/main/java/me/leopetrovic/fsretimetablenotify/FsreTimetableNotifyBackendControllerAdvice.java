package me.leopetrovic.fsretimetablenotify;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import me.leopetrovic.fsretimetablenotify.common.dto.FsreError;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Order(2)
@ControllerAdvice
public class FsreTimetableNotifyBackendControllerAdvice {
    // Global exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<FsreError> handleException(Exception e) {
        if (AnnotationUtils.findAnnotation(e.getClass(),
            ResponseStatus.class) != null) {
            throw (RuntimeException) e;
        }

        final FsreError fsreException = new FsreError(HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            e.getMessage());

        return ResponseEntity.status(fsreException.getStatus())
            .body(fsreException);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponses(
        value = {@ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FsreError.class)
            )}
        )}
    )
    public ResponseEntity<FsreError> handleMissingServletRequestParameterException(
        MissingServletRequestParameterException e
    ) {
        final FsreError fsreException = new FsreError(HttpStatus.BAD_REQUEST,
            "Bad Request",
            e.getMessage());

        return ResponseEntity.status(fsreException.getStatus())
            .body(fsreException);
    }
}
