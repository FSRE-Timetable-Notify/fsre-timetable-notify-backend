package me.leopetrovic.fsretimetablenotify.timetable.dto;

import org.threeten.extra.YearWeek;

public record ExternalFsreTimetableRequest(
    Long studyProgramId,
    YearWeek yearWeek
) {}