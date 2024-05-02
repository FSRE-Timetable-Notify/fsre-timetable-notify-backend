package me.leopetrovic.fsretimetablenotify.timetabledatabase;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Year;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.leopetrovic.fsretimetablenotify.common.properties.FsreTimetableNotifyProperties;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.dto.ExternalFsreTimetableDatabaseRequest;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.exceptions.TimetableDatabaseFetchException;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.exceptions.TimetableDatabaseNotFetchedException;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.exceptions.TimetableDatabaseParseException;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.TimetableDatabase;

@Service
public class TimetableDatabaseService {
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private InMemoryTimetableDatabaseStore inMemoryTimetableDatabaseStore;

	@Autowired
	FsreTimetableNotifyProperties fsreTimetableNotifyProperties;

	public TimetableDatabase getTimetableDatabase() {
		return inMemoryTimetableDatabaseStore.getTimetableDatabase()
				.orElseThrow(() -> new TimetableDatabaseNotFetchedException());
	}

	public void setTimetableDatabase(TimetableDatabase timetableDatabase) {
		inMemoryTimetableDatabaseStore.setTimetableDatabase(timetableDatabase);
	}

	@Async
	public CompletableFuture<TimetableDatabase> fetchTimetableDatabase() {
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(fsreTimetableNotifyProperties.timetableDbUri())
				.POST(HttpRequest.BodyPublishers.ofString(toJson(new ExternalFsreTimetableDatabaseRequest(Year.now()))))
				.build();

		return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
				.thenApply(HttpResponse::body)
				.handle((body, throwable) -> {
					if (throwable != null) {
						throw new CompletionException(new TimetableDatabaseFetchException(throwable));
					}

					try {
						return objectMapper.readValue(body, TimetableDatabase.class);
					} catch (JsonProcessingException e) {
						throw new CompletionException(new TimetableDatabaseParseException(e));
					}
				});

	}

	private String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
