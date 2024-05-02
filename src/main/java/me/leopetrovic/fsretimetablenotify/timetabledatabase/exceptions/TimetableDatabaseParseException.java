package me.leopetrovic.fsretimetablenotify.timetabledatabase.exceptions;

public class TimetableDatabaseParseException extends RuntimeException {
	public TimetableDatabaseParseException() {
		super("Failed to parse timetable database");
	}

	public TimetableDatabaseParseException(Throwable cause) {
		super("Failed to parse timetable database", cause);
	}
}
