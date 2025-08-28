package me.leopetrovic.fsretimetablenotify.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import me.leopetrovic.fsretimetablenotify.common.properties.FsreTimetableNotifyProperties;
import me.leopetrovic.fsretimetablenotify.timetable.dto.ExternalFsreTimetableRequest;
import me.leopetrovic.fsretimetablenotify.timetable.exceptions.TimetableFetchException;
import me.leopetrovic.fsretimetablenotify.timetable.exceptions.TimetableParseException;
import me.leopetrovic.fsretimetablenotify.timetable.models.Timetable;
import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableEvent;
import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableKey;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.TimetableDatabaseService;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.StudyProgram;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.TimetableDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class TimetableService {
    private final FsreTimetableNotifyProperties fsreTimetableNotifyProperties;
    private final ObjectMapper objectMapper;
    private final InMemoryTimetableStore inMemoryTimetableStore;
    private final TimetableDatabaseService timetableDatabaseService;

    @Autowired
    public TimetableService(
        FsreTimetableNotifyProperties fsreTimetableNotifyProperties,
        ObjectMapper objectMapper,
        InMemoryTimetableStore inMemoryTimetableStore,
        TimetableDatabaseService timetableDatabaseService
    ) {
        this.fsreTimetableNotifyProperties = fsreTimetableNotifyProperties;
        this.objectMapper = objectMapper;
        this.inMemoryTimetableStore = inMemoryTimetableStore;
        this.timetableDatabaseService = timetableDatabaseService;
    }

    public Optional<Timetable> getTimetable(
        @Nonnull
        TimetableKey timetableKey
    ) {
        return inMemoryTimetableStore.getTimetable(timetableKey);
    }

    public void setTimetable(
        @Nonnull
        TimetableKey timetableKey,
        @Nonnull
        Timetable timetable
    ) {
        inMemoryTimetableStore.setTimetable(timetableKey, timetable);
    }

    @Async
    public CompletableFuture<Timetable> getOrFetchTimetable(
        @Nonnull
        TimetableKey timetableKey
    ) {
        final Optional<Timetable> maybeTimetable = getTimetable(timetableKey);

        if (maybeTimetable.isPresent()) {
            return CompletableFuture.completedFuture(maybeTimetable.get());
        }

        try {
            return fetchTimetable(timetableKey).thenApply(timetableResponse -> {
                setTimetable(timetableKey, timetableResponse);
                return timetableResponse;
            });
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof TimetableFetchException) {
                throw (TimetableFetchException) cause;
            } else if (cause instanceof TimetableParseException) {
                throw (TimetableParseException) cause;
            } else {
                throw new RuntimeException(cause);
            }
        }
    }

    @Async
    public CompletableFuture<Timetable> fetchTimetable(
        @Nonnull
        TimetableKey timetableKey
    ) {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            var studyProgramIds = timetableKey.studyProgramId() != null
                ? List.of(timetableKey.studyProgramId())
                : timetableDatabaseService.getTimetableDatabase()
                    .getStudyPrograms()
                    .stream()
                    .map(StudyProgram::id)
                    .toList();

            var futures = studyProgramIds.stream().map(studyProgramId -> {
                HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(fsreTimetableNotifyProperties.timetableUri())
                    .POST(HttpRequest.BodyPublishers.ofString(toJson(new ExternalFsreTimetableRequest(
                        studyProgramId,
                        timetableKey.yearWeek()))))
                    .build();

                return httpClient.sendAsync(httpRequest,
                        HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .handle((body, throwable) -> {
                        if (throwable != null) {
                            throw new CompletionException(new TimetableFetchException(
                                throwable));
                        }

                        try {
                            return extendTimetable(objectMapper.readValue(body,
                                Timetable.class));
                        } catch (JsonProcessingException e) {
                            throw new CompletionException(new TimetableParseException(
                                e));
                        }
                    });
            }).toList();

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                    .map(CompletableFuture::join)
                    .reduce((t1, t2) -> {
                        t1.merge(t2);
                        return t1;
                    }).orElseThrow(TimetableFetchException::new));
        } catch (Exception e) {
            throw new CompletionException(new TimetableFetchException(e));
        }
    }

    private Timetable extendTimetable(
        @Nonnull
        Timetable timetable
    ) {
        final TimetableDatabase timetableDatabase = timetableDatabaseService.getTimetableDatabase();

        for (List<TimetableEvent> eventsForWeekDay : timetable.getWeekDays()) {
            for (TimetableEvent timetableEvent : eventsForWeekDay) {
                timetableEvent.getTeacherIds()
                    .forEach(teacherId -> timetableEvent.addTeacherName(
                        timetableDatabase.getTeacherName(teacherId)));

                timetableEvent.getClassRoomIds()
                    .forEach(classRoomId -> timetableEvent.addClassRoomName(
                        timetableDatabase.getClassRoomName(classRoomId)));

                timetableEvent.getStudyProgramIds()
                    .forEach(studyProgramId -> timetableEvent.addStudyProgramName(
                        timetableDatabase.getStudyProgramName(studyProgramId)));
            }
        }

        return timetable;
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
