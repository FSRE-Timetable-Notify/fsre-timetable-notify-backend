package me.leopetrovic.fsretimetablenotify.timetabledatabase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.TimetableDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timetable-database")
@Validated
public class TimetableDatabaseController {
    @Autowired
    private TimetableDatabaseService timetableDatabaseService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the timetable definitions database for the current study year")
    @ApiResponses(
        value = {@ApiResponse(
            responseCode = "200",
            description = "Timetable database retrieved successfully",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TimetableDatabase.class)
            )}
        )}
    )
    public ResponseEntity<TimetableDatabase> getTimetableDatabase() {
        return ResponseEntity.ok(timetableDatabaseService.getTimetableDatabase());
    }
}
