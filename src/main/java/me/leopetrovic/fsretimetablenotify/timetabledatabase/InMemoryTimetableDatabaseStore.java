package me.leopetrovic.fsretimetablenotify.timetabledatabase;

import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.TimetableDatabase;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
