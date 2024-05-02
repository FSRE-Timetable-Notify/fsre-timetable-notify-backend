package me.leopetrovic.fsretimetablenotify.timetabledatabase.exceptions;

public class TimetableDatabaseFetchException extends RuntimeException {
	public TimetableDatabaseFetchException() {
		super("Failed to fetch timetable database");
	}

	public TimetableDatabaseFetchException(Throwable cause) {
		super("Failed to fetch timetable database", cause);
	}
}
