package me.leopetrovic.fsretimetablenotify.timetable;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.leopetrovic.fsretimetablenotify.common.properties.FsreTimetableNotifyProperties;
import me.leopetrovic.fsretimetablenotify.timetable.dto.ExternalFsreTimetableRequest;
import me.leopetrovic.fsretimetablenotify.timetable.exceptions.TimetableFetchException;
import me.leopetrovic.fsretimetablenotify.timetable.exceptions.TimetableParseException;
import me.leopetrovic.fsretimetablenotify.timetable.models.Timetable;
import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableEvent;
import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableKey;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.TimetableDatabaseService;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.TimetableDatabase;

@Service
public class TimetableService {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private InMemoryTimetableStore inMemoryTimetableStore;

	@Autowired
	private TimetableDatabaseService timetableDatabaseService;

	@Autowired
	FsreTimetableNotifyProperties fsreTimetableNotifyProperties;

	public Optional<Timetable> getTimetable(@NonNull TimetableKey timetableKey) {
		return inMemoryTimetableStore.getTimetable(timetableKey);
	}

	public void setTimetable(@NonNull TimetableKey timetableKey, @NonNull Timetable timetable) {
		inMemoryTimetableStore.setTimetable(timetableKey, timetable);
	}

	@Async
	public CompletableFuture<Timetable> getOrFetchTimetable(@NonNull TimetableKey timetableKey) {
		final Optional<Timetable> maybeTimetable = getTimetable(timetableKey);

		if (maybeTimetable.isPresent()) {
			return CompletableFuture.completedFuture(maybeTimetable.get());
		}

		try {
			return fetchTimetable(timetableKey)
					.thenApply(timetableResponse -> {
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
	public CompletableFuture<Timetable> fetchTimetable(@NonNull TimetableKey timetableKey) {
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(fsreTimetableNotifyProperties.timetableUri())
				.POST(HttpRequest.BodyPublishers.ofString(toJson(new ExternalFsreTimetableRequest(
						timetableKey.studyProgramId(), timetableKey
								.yearWeek()))))
				.build();

		return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
				.thenApply(HttpResponse::body)
				.handle((body, throwable) -> {
					if (throwable != null) {
						throw new CompletionException(new TimetableFetchException(throwable));
					}

					try {
						return extendTimetable(objectMapper.readValue(body, Timetable.class));
					} catch (JsonProcessingException e) {
						throw new CompletionException(new TimetableParseException(e));
					}
				});

	}

	private Timetable extendTimetable(@NonNull Timetable timetable) {
		final TimetableDatabase timetableDatabase = timetableDatabaseService.getTimetableDatabase();

		for (List<TimetableEvent> eventsForWeekDay : timetable.getWeekDays()) {
			for (TimetableEvent timetableEvent : eventsForWeekDay) {
				timetableEvent.getTeacherIds().forEach(teacherId -> {
					timetableEvent.addTeacherName(timetableDatabase.getTeacherName(teacherId));
				});

				timetableEvent.getClassRoomIds().forEach(classRoomId -> {
					timetableEvent.addClassRoomName(timetableDatabase.getClassRoomName(classRoomId));
				});

				timetableEvent.getStudyProgramIds().forEach(studyProgramId -> {
					timetableEvent.addStudyProgramName(timetableDatabase.getStudyProgramName(studyProgramId));
				});
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
