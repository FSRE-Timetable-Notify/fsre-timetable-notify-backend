package me.leopetrovic.fsretimetablenotify.timetable.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "The timetable for a week")
public class Timetable {
	@Schema(description = "The event list for Monday")
	private List<TimetableEvent> monday;

	@Schema(description = "The event list for Tuesday")
	private List<TimetableEvent> tuesday;

	@Schema(description = "The event list for Wednesday")
	private List<TimetableEvent> wednesday;

	@Schema(description = "The event list for Thursday")
	private List<TimetableEvent> thursday;

	@Schema(description = "The event list for Friday")
	private List<TimetableEvent> friday;

	@Schema(description = "The event list for Saturday")
	private List<TimetableEvent> saturday;

	@Schema(description = "The event list for Sunday")
	private List<TimetableEvent> sunday;

	@JsonIgnore
	public List<List<TimetableEvent>> getWeekDays() {
		return List.of(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
	}

	@JsonIgnore
	public List<TimetableEvent> getEvents() {
		return List.of(monday, tuesday, wednesday, thursday, friday, saturday, sunday).stream()
				.flatMap(List::stream)
				.toList();
	}

	public void setWeekDays(List<List<TimetableEvent>> weekDays) {
		monday = weekDays.get(0);
		tuesday = weekDays.get(1);
		wednesday = weekDays.get(2);
		thursday = weekDays.get(3);
		friday = weekDays.get(4);
		saturday = weekDays.get(5);
		sunday = weekDays.get(6);
	}
}