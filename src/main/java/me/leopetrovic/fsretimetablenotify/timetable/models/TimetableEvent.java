package me.leopetrovic.fsretimetablenotify.timetable.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "A single event in the timetable")
public class TimetableEvent {
    @Schema(
        description = "The ID of the event",
        requiredMode = RequiredMode.REQUIRED
    )
    private Long id;

    @Schema(
        description = "The department which the event belongs to",
        requiredMode = RequiredMode.REQUIRED
    )
    private TimetableEventDepartment department;

    @Schema(
        description = "The type of the event (e.g. lecture, exercise, labs)",
        requiredMode = RequiredMode.REQUIRED
    )
    private TimetableEventType type;

    @Schema(
        description = "The year of study the event is intended for",
        requiredMode = RequiredMode.REQUIRED
    )
    private TimetableEventYear year;

    @Schema(
        description = "Name of the event", requiredMode = RequiredMode.REQUIRED
    )
    private String name;

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
        timezone = "UTC"
    )
    @Schema(
        description = "Start date and time of the event",
        requiredMode = RequiredMode.REQUIRED
    )
    private ZonedDateTime startDateTime;

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
        timezone = "UTC"
    )
    @Schema(
        description = "End date and time of the event",
        requiredMode = RequiredMode.REQUIRED
    )
    private ZonedDateTime endDateTime;

    @Schema(
        description = "The IDs of the study programs this event is intended for",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<Long> studyProgramIds;

    @Schema(
        description = "The IDs of the classrooms the event will take place in",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<Long> classRoomIds;

    @Schema(
        description = "The IDs of the teachers that will teach this event",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<Long> teacherIds;

    @Schema(
        description = "The readable names of the study programs this event is intended for",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<String> studyProgramNames = new ArrayList<>();

    @Schema(
        description = "The readable names of the classrooms the event will take place in",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<String> classRoomNames = new ArrayList<>();

    @Schema(
        description = "The readable names of the teachers that will teach this event",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<String> teacherNames = new ArrayList<>();

    public void addStudyProgramName(String studyProgramName) {
        this.studyProgramNames.add(studyProgramName);
    }

    public void addClassRoomName(String classRoomName) {
        this.classRoomNames.add(classRoomName);
    }

    public void addTeacherName(String teacherName) {
        this.teacherNames.add(teacherName);
    }

    @Schema(description = "The department which the timetable event belongs to")
    public enum TimetableEventDepartment {
        COMPUTER_SCIENCE,
        ELECTRICAL_ENGINEERING,
        MECHANICAL_ENGINEERING,
    }

    @Schema(description = "The type of the timetable event")
    public enum TimetableEventType {
        LECTURE,
        EXERCISE,
        LECTURE_AND_EXERCISE,
        LABS,
    }

    @Schema(description = "The year of study the timetable event is intended for")
    public enum TimetableEventYear {
        FIRST,
        SECOND,
        THIRD,
        FOURTH,
        FIFTH
    }

}