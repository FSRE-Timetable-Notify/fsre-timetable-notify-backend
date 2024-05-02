package me.leopetrovic.fsretimetablenotify.timetabledatabase.models;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "The database of timetable definitions (names of subjects, teachers, etc.)")
public class TimetableDatabase {
	@Schema(description = "List of study programs (id of the study program and readable name)")
	private List<IdNamePair<Long>> studyPrograms;

	@Schema(description = "List of classrooms (id of the classroom and readable name)")
	private List<IdNamePair<Long>> classRooms;

	@Schema(description = "List of event types (id of the event type and readable name)")
	private List<IdNamePair<String>> eventTypes;

	@Schema(description = "List of subjects (id of the subject and readable name)")
	private List<IdNamePair<Long>> subjects;

	@Schema(description = "List of teachers (id of the teacher and readable name)")
	private List<IdNamePair<Long>> teachers;

	@Data
	@Schema(description = "A pair of an ID and a name, used in the timetable database")
	public static class IdNamePair<T> {
		private T id;
		private String name;
	}

	public String getStudyProgramName(Long id) {
		return getNameById(studyPrograms, id);
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
