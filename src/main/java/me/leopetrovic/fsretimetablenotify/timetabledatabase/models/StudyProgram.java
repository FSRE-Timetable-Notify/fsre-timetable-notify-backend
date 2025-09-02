package me.leopetrovic.fsretimetablenotify.timetabledatabase.models;

import io.swagger.v3.oas.annotations.media.Schema;
import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableEvent;

@Schema(description = "A study program characterized by study year and department")
public record StudyProgram(
    @Schema(
        description = "The ID of the study program",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    Long id,

    @Schema(
        description = "The full name of the study program",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    String name,

    @Schema(
        description = "The study year the program is intended for",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    TimetableEvent.TimetableEventYear studyYear,

    @Schema(
        description = "The department which the study program belongs to",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    TimetableEvent.TimetableEventDepartment department,

    @Schema(
        description = "The direction/specialization which the study program " +
            "belongs to",
        requiredMode = Schema.RequiredMode.REQUIRED,
        nullable = true
    )
    String direction
) {}
