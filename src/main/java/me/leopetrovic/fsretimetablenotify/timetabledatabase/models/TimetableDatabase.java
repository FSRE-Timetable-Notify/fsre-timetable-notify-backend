package me.leopetrovic.fsretimetablenotify.timetabledatabase.models;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "The database of timetable definitions (names of subjects, teachers, etc.)")
public class TimetableDatabase {
    @Schema(
        description = "List of study programs",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<StudyProgram> studyPrograms;

    @Schema(
        description = "List of classrooms (id of the classroom and readable name)",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<IdNamePair<Long>> classRooms;

    @Schema(
        description = "List of event types (id of the event type and readable name)",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<IdNamePair<String>> eventTypes;

    @Schema(
        description = "List of subjects (id of the subject and readable name)",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<IdNamePair<Long>> subjects;

    @Schema(
        description = "List of teachers (id of the teacher and readable name)",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<IdNamePair<Long>> teachers;

    public String getStudyProgramName(Long id) {
        return studyPrograms.stream()
            .filter(sp -> sp.id().equals(id))
            .map(StudyProgram::name)
            .findFirst()
            .orElse(null);
    }

    public String getClassRoomName(Long id) {
        return getNameById(classRooms, id);
    }

    public String getEventTypeName(String id) {
        return getNameById(eventTypes, id);
    }

    public String getSubjectName(Long id) {
        return getNameById(subjects, id);
    }

    public String getTeacherName(Long id) {
        return getNameById(teachers, id);
    }

    private <T> String getNameById(List<IdNamePair<T>> list, T id) {
        return list.stream()
            .filter(pair -> pair.getId().equals(id))
            .map(IdNamePair::getName)
            .findFirst()
            .orElse(null);
    }
}
