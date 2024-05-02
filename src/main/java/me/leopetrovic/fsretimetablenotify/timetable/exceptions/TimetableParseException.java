package me.leopetrovic.fsretimetablenotify.timetable.exceptions;

public class TimetableParseException extends RuntimeException {
	public TimetableParseException() {
		super("Failed to parse timetable");
	}

	public TimetableParseException(Throwable cause) {
		super("Failed to parse timetable", cause);
	}
}
