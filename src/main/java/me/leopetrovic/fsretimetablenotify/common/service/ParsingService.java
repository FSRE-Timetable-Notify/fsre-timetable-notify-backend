package me.leopetrovic.fsretimetablenotify.common.service;

import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParsingService {
    public TimetableEvent.TimetableEventDepartment determineTimetableEventDepartment(
        String studyProgramName
    ) {
        var computerEngineering = "računarstv";
        var electricalEngineering = "elektrotehnik";
        var mechanicalEngineering = "strojarstv";

        if (studyProgramName.toLowerCase().contains(computerEngineering)) {
            return TimetableEvent.TimetableEventDepartment.COMPUTER_SCIENCE;
        } else if (studyProgramName.toLowerCase()
            .contains(electricalEngineering)) {
            return TimetableEvent.TimetableEventDepartment.ELECTRICAL_ENGINEERING;
        } else if (studyProgramName.toLowerCase()
            .contains(mechanicalEngineering)) {
            return TimetableEvent.TimetableEventDepartment.MECHANICAL_ENGINEERING;
        } else {
            return null;
        }
    }

    public TimetableEvent.TimetableEventYear determineTimetableEventYear(String studyProgramName) {
        var yearRegex = ".*-?(\\s*)?(\\d)\\.? ?(godina|god).*?$";
        var undergraduateStudyProgram = "preddiplomsk";

        if (!studyProgramName.matches(yearRegex)) {
            return null;
        }

        switch (studyProgramName.replaceAll(yearRegex, "$2")) {
            case "1" -> {
                if (studyProgramName.toLowerCase()
                    .contains(undergraduateStudyProgram)) {
                    return TimetableEvent.TimetableEventYear.FIRST;
                } else {
                    return TimetableEvent.TimetableEventYear.FOURTH;
                }
            }
            case "2" -> {
                if (studyProgramName.toLowerCase()
                    .contains(undergraduateStudyProgram)) {
                    return TimetableEvent.TimetableEventYear.SECOND;
                } else {
                    return TimetableEvent.TimetableEventYear.FIFTH;
                }
            }
            case "3" -> {
                return TimetableEvent.TimetableEventYear.THIRD;
            }
            case "4" -> {
                return TimetableEvent.TimetableEventYear.FOURTH;
            }
            case "5" -> {
                return TimetableEvent.TimetableEventYear.FIFTH;
            }
            default -> {
                return null;
            }
        }
    }

    public List<String> determineTimetableEventDirections(List<String> studyProgramNames) {
        var directionRegex = "^.+(godina, |god |god\\.)(smjer|modul)?(.+)$";

        var matches = new ArrayList<String>();
        for (String studyProgramName : studyProgramNames) {
            if (!studyProgramName.matches(directionRegex)) {
                matches.add(null);
                continue;
            }

            var extractedDirection = studyProgramName.replaceAll(directionRegex,
                "$3").trim();
            var cleanedUpDirection = extractedDirection.replaceAll("[^a-zA-Z0-9 ]",
                "").trim();
            matches.add(cleanedUpDirection.isEmpty() ? null : cleanedUpDirection);
        }

        return matches.stream().distinct().toList();
    }

    public TimetableEvent.TimetableEventType determineTimetableEventType(String subject) {
        var regex = ".*-(\\s*)?(P|V|P\\+V|V\\+P)(\\s*)?$";
        switch (subject.replaceAll(regex, "$2")) {
            case "V" -> {
                return TimetableEvent.TimetableEventType.EXERCISE;
            }
            case "P+V", "V+P" -> {
                return TimetableEvent.TimetableEventType.LECTURE_AND_EXERCISE;
            }
            default -> {
                return TimetableEvent.TimetableEventType.LECTURE;
            }
        }
    }
}
