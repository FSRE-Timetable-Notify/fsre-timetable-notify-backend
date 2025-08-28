package me.leopetrovic.fsretimetablenotify.timetable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import me.leopetrovic.fsretimetablenotify.timetable.models.Timetable;
import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.threeten.extra.YearWeek;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/timetable")
@Validated
public class TimetableController {
    private final TimetableService timetableService;

    @Autowired
    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the timetable data for a study program")
    @ApiResponses(
        value = {@ApiResponse(
            responseCode = "200",
            description = "Timetable data retrieved successfully",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Timetable.class)
            )}
        )}
    )
    public CompletableFuture<ResponseEntity<Timetable>> getTimetable(
        @Nullable
        @RequestParam
        @Parameter(
            description = "Study program", example = "-54"
        )
        Long studyProgram,
        @NotNull
        @RequestParam
        @Parameter(
            description = "ISO week", required = true, example = "2024-W09"
        )
        String isoWeek
    ) {
        final TimetableKey timetableKey = new TimetableKey(studyProgram,
            YearWeek.parse(isoWeek));

        return timetableService.getOrFetchTimetable(timetableKey).thenApply(ResponseEntity::ok);
    }
}
