package me.leopetrovic.fsretimetablenotify.timetabledatabase;

import lombok.Getter;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.TimetableDatabase;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Getter
@Component
public class InMemoryTimetableDatabaseStore {
    private Optional<TimetableDatabase> timetableDatabase = Optional.empty();

    public void setTimetableDatabase(TimetableDatabase timetableDatabase) {
        this.timetableDatabase = Optional.of(timetableDatabase);
    }
}
