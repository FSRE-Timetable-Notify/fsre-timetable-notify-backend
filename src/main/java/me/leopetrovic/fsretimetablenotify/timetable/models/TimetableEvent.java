package me.leopetrovic.fsretimetablenotify.timetable.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "A single event in the timetable")
public class TimetableEvent {
	@Schema(description = "The ID of the event")
	private Long id;

	@Schema(description = "Name of the event")
	private String name;

	@Schema(description = "Start date and time of the event")
	private Instant startDate;

	@Schema(description = "End date and time of the event")
	private Instant endDate;

	@Schema(description = "The IDs of the study programs this event is intended for")
	private List<Long> studyProgramIds;

	@Schema(description = "The IDs of the classrooms the event will take place in")
	private List<Long> classRoomIds;

	@Schema(description = "The IDs of the teachers that will teach this event")
	private List<Long> teacherIds;

	@Schema(description = "The readable names of the study programs this event is intended for")
	private List<String> studyProgramNames = new ArrayList<>();

	@Schema(description = "The readable names of the classrooms the event will take place in")
	private List<String> classRoomNames = new ArrayList<>();

	@Schema(description = "The readable names of the teachers that will teach this event")
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
}