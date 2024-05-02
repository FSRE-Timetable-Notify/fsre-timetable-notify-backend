package me.leopetrovic.fsretimetablenotify.timetable.models;

import org.threeten.extra.YearWeek;

public record TimetableKey(Long studyProgramId, YearWeek yearWeek) {
}
