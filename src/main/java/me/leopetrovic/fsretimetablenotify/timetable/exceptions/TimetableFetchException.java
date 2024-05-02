package me.leopetrovic.fsretimetablenotify.timetable.exceptions;

public class TimetableFetchException extends RuntimeException {
	public TimetableFetchException() {
		super("Failed to fetch timetable");
	}

	public TimetableFetchException(Throwable cause) {
		super("Failed to fetch timetable", cause);
	}
}
