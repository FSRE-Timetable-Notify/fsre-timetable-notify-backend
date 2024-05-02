package me.leopetrovic.fsretimetablenotify.timing;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.threeten.extra.YearWeek;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.leopetrovic.fsretimetablenotify.messaging.MessagingService;
import me.leopetrovic.fsretimetablenotify.messaging.dto.TimetableUpdatedMessageDto;
import me.leopetrovic.fsretimetablenotify.messaging.models.MessagingSubscription;
import me.leopetrovic.fsretimetablenotify.timetable.TimetableService;
import me.leopetrovic.fsretimetablenotify.timetable.models.Timetable;
import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableEvent;
import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableKey;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.TimetableDatabaseService;

@Slf4j
@Service
public class TimingService {
	@Autowired
	private MessagingService messagingService;

	@Autowired
	private TimetableDatabaseService timetableDatabaseService;

	@Autowired
	private TimetableService timetableService;

	@Scheduled(cron = "30 * * * * *")
	public void refreshTimetables() {
		// For each study program
		log.debug("Refreshing timetables for "
				+ timetableDatabaseService.getTimetableDatabase().getStudyPrograms().size() + " study programs");
		timetableDatabaseService.getTimetableDatabase().getStudyPrograms().forEach(studyProgram -> {
			final List<MessagingSubscription> subscribers = messagingService.getAllByStudyProgramId(studyProgram.getId());

			// If there are no subscriptions for the study program, skip
			if (subscribers.isEmpty()) {
				log.debug("No subscriptions found for study program " + studyProgram.getId() + ", skipping");
				return;
			} else {
				log.debug("Found " + subscribers.size()
						+ " subscriptions for study program " + studyProgram.getId());
			}

			// Else, fetch the timetable for the current and next week
			final TimetableKey currentTimetableKey = new TimetableKey(studyProgram.getId(), YearWeek.now());
			final TimetableKey nextTimetableKey = new TimetableKey(studyProgram.getId(), YearWeek.now().plusWeeks(1));

			for (TimetableKey timetableKey : new TimetableKey[] { currentTimetableKey, nextTimetableKey }) {
				log.debug("Fetching timetable for " + timetableKey);
				timetableService.fetchTimetable(
						timetableKey)
						.thenAccept(newTimetable -> {
							// If a previously cached timetable exists and is different from the previous
							// fetch, send a message
							final Optional<Timetable> maybeExistingTimetable = timetableService.getTimetable(timetableKey);
							if (maybeExistingTimetable.isPresent()) {
								final Timetable existingTimetable = maybeExistingTimetable.get();

								final TimetableDifference difference = getDifference(existingTimetable, newTimetable);
								if (!difference.newEvents.isEmpty() || !difference.removedEvents.isEmpty()) {
									log.debug("Timetable for " + timetableKey + " has changed - "
											+ (difference.newEvents.size() + difference.removedEvents.size()) + " changes");

									// Send a message to each subscriber of the study program
									messagingService.getAllByStudyProgramId(studyProgram.getId()).forEach(messagingSubscription -> {
										log.debug("Sending message...");

										messagingService.sendMessage(messagingSubscription.getFcmToken(), new TimetableUpdatedMessageDto(
												difference,
												timetableKey));
									});
								} else {
									log.debug("Timetable for " + timetableKey + " has not changed, skipping message");
								}

								timetableService.setTimetable(timetableKey, newTimetable);
							} else {
								log
										.debug("No previous timetable found for " + timetableKey + " found, skipping message and caching");
								timetableService.setTimetable(timetableKey, newTimetable);
							}
						}).join();
			}
		});

	}

	private TimetableDifference getDifference(Timetable existingTimetable, Timetable newTimetable) {
		Set<TimetableEvent> newEvents = new HashSet<>();
		Set<TimetableEvent> removedEvents = new HashSet<>();

		// Find new events
		for (List<TimetableEvent> eventsForWeekDay : newTimetable.getWeekDays()) {
			for (TimetableEvent newEvent : eventsForWeekDay) {
				if (!existingTimetable.getWeekDays().stream().flatMap(List::stream)
						.anyMatch(existingEvent -> existingEvent.equals(newEvent))) {
					newEvents.add(newEvent);
				}
			}
		}

		// Find removed events
		for (List<TimetableEvent> eventsForWeekDay : existingTimetable.getWeekDays()) {
			for (TimetableEvent existingEvent : eventsForWeekDay) {
				if (!newTimetable.getWeekDays().stream().flatMap(List::stream)
						.anyMatch(newEvent -> newEvent.equals(existingEvent))) {
					removedEvents.add(existingEvent);
				}
			}
		}

		return new TimetableDifference(newEvents, removedEvents);
	}

	@Data
	public static class TimetableDifference {
		private final Set<TimetableEvent> newEvents;
		private final Set<TimetableEvent> removedEvents;
	}
}
