package me.leopetrovic.fsretimetablenotify.timetabledatabase.exceptions;

public class TimetableDatabaseNotFetchedException extends RuntimeException {
    public TimetableDatabaseNotFetchedException() {
        super(
            "Cannot retrieve the timetable database as it has not been fetched yet");
    }

    public TimetableDatabaseNotFetchedException(Throwable cause) {
        super(
            "Cannot retrieve the timetable database as it has not been fetched yet",
            cause);
    }
}
