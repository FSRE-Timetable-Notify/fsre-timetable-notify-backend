package me.leopetrovic.fsretimetablenotify.timetable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import me.leopetrovic.fsretimetablenotify.timetable.models.Timetable;
import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableKey;

@Component
public class InMemoryTimetableStore {
	private final Map<TimetableKey, Timetable> timetables = new HashMap<>();

	public Optional<Timetable> getTimetable(TimetableKey timetableKey) {
		return Optional.ofNullable(timetables.get(timetableKey));
	}

	public void setTimetable(TimetableKey timetableKey, Timetable timetable) {
		timetables.put(timetableKey, timetable);
	}
}
