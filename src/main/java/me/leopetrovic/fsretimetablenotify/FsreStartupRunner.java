package me.leopetrovic.fsretimetablenotify;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import me.leopetrovic.fsretimetablenotify.timetable.TimetableService;
import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableKey;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.TimetableDatabaseService;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.TimetableDatabase.IdNamePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.threeten.extra.YearWeek;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class FsreStartupRunner implements ApplicationRunner {
    @Autowired
    private TimetableDatabaseService timetableDatabaseService;

    @Autowired
    private TimetableService timetableService;

    @Override
    public void run(ApplicationArguments args) throws IOException {
        log.info("Fetching timetable database...");
        timetableDatabaseService.fetchTimetableDatabase()
            .thenAccept(timetableDatabase -> timetableDatabaseService.setTimetableDatabase(
                timetableDatabase))
            .join();
        log.info("Timetable database fetched!");

        if (FirebaseApp.getApps().isEmpty()) {
            try {
                var credentials = GoogleCredentials.getApplicationDefault();
                log.info("Initializing Firebase...");
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();
                FirebaseApp.initializeApp(options);
            } catch (IOException e) {
                log.warn("Failed to obtain Google Application Default " +
                    "Credentials, Firebase will not be initialized.");
            }

        }

        final List<IdNamePair<Long>> studyPrograms = timetableDatabaseService.getTimetableDatabase()
            .getStudyPrograms();
        log.info(
            "Populating timetable cache for next 2 weeks for {} study programs...",
            studyPrograms.size());

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        studyPrograms.forEach(studyProgram -> {
            final TimetableKey currentTimetableKey = new TimetableKey(
                studyProgram.getId(),
                YearWeek.now());
            final TimetableKey nextTimetableKey = new TimetableKey(studyProgram.getId(),
                YearWeek.now().plusWeeks(1));

            for (TimetableKey timetableKey : new TimetableKey[]{
                currentTimetableKey, nextTimetableKey}) {
                log.debug("Caching timetable for " + timetableKey);
                CompletableFuture<Void> future = timetableService.fetchTimetable(
                    timetableKey).thenAccept(newTimetable -> {
                    timetableService.setTimetable(timetableKey, newTimetable);
                });
                futures.add(future);
            }
        });
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .join();
        log.info("Timetable cache populated!");
    }
}