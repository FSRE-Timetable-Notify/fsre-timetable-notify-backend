package me.leopetrovic.fsretimetablenotify.messaging.dto;

import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableKey;
import me.leopetrovic.fsretimetablenotify.timing.TimingService.TimetableDifference;

public record TimetableUpdatedMessageDto(TimetableDifference difference, TimetableKey timetableKey) {
}
