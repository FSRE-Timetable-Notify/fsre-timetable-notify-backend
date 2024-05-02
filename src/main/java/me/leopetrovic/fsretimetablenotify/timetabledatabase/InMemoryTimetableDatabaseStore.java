package me.leopetrovic.fsretimetablenotify.timetabledatabase;

import java.util.Optional;

import org.springframework.stereotype.Component;

import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.TimetableDatabase;

@Component
public class InMemoryTimetableDatabaseStore {
	private Optional<TimetableDatabase> timetableDatabase = Optional.empty();

	public Optional<TimetableDatabase> getTimetableDatabase() {
		return timetableDatabase;
	}

	public void setTimetableDatabase(TimetableDatabase timetableDatabase) {
		this.timetableDatabase = Optional.of(timetableDatabase);
	}
}
