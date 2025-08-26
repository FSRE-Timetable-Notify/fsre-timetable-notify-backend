package me.leopetrovic.fsretimetablenotify.timetable.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "The timetable for a week")
public class Timetable {
    @Schema(
        description = "The event list for Monday",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<TimetableEvent> monday;

    @Schema(
        description = "The event list for Tuesday",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<TimetableEvent> tuesday;

    @Schema(
        description = "The event list for Wednesday",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<TimetableEvent> wednesday;

    @Schema(
        description = "The event list for Thursday",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<TimetableEvent> thursday;

    @Schema(
        description = "The event list for Friday",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<TimetableEvent> friday;

    @Schema(
        description = "The event list for Saturday",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<TimetableEvent> saturday;

    @Schema(
        description = "The event list for Sunday",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<TimetableEvent> sunday;

    @JsonIgnore
    public List<List<TimetableEvent>> getWeekDays() {
        return List.of(monday,
            tuesday,
            wednesday,
            thursday,
            friday,
            saturday,
            sunday);
    }
}