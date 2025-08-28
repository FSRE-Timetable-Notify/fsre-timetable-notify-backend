package me.leopetrovic.fsretimetablenotify.timetable.transformers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import me.leopetrovic.fsretimetablenotify.timetable.models.Timetable;
import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableEvent;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.TimetableDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonComponent
public class TimetableDeserializer extends JsonDeserializer<Timetable> {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(
        "HH:mm");

    private final TimetableDatabaseService timetableDatabaseService;

    @Autowired
    public TimetableDeserializer(TimetableDatabaseService timetableDatabaseService) {
        this.timetableDatabaseService = timetableDatabaseService;
    }

    @Override
    public Timetable deserialize(
        JsonParser p,
        DeserializationContext ctx
    ) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        JsonNode itemsNode = node.get("r").get("ttitems");

        Map<DayOfWeek, List<TimetableEvent>> eventsByDayOfWeek = new HashMap<>();

        for (JsonNode itemNode : itemsNode) {
            String type = itemNode.get("type").asText();
            if (!"event".equals(type)) {
                continue;
            }

            String dateStr = itemNode.get("date").asText();
            LocalDate date = LocalDate.parse(dateStr);
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            String startTimeStr = itemNode.get("starttime").asText();
            LocalTime startTime = LocalTime.parse(startTimeStr, TIME_FORMATTER);

            String endTimeStr = itemNode.get("endtime").asText();
            LocalTime endTime = LocalTime.parse(endTimeStr, TIME_FORMATTER);

            LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

            ZoneId bihZone = ZoneId.of("Europe/Sarajevo");
            ZonedDateTime startZoned = startDateTime.atZone(bihZone);
            ZonedDateTime endZoned = endDateTime.atZone(bihZone);

            ZonedDateTime utcStart = startZoned.withZoneSameInstant(ZoneOffset.UTC);
            ZonedDateTime utcEnd = endZoned.withZoneSameInstant(ZoneOffset.UTC);

            String name = itemNode.get("name").asText();

            Long id = Long.parseLong(itemNode.get("subjectid").asText());

            List<Long> studyProgramIds = parseIds(itemNode.get("classids"));
            List<Long> classRoomIds = parseIds(itemNode.get("classroomids"));
            List<Long> teacherIds = parseIds(itemNode.get("teacherids"));

            List<String> studyProgramNames = studyProgramIds.stream()
                .map(studyProgramId -> timetableDatabaseService.getTimetableDatabase()
                    .getStudyProgramName(studyProgramId))
                .toList();

            TimetableEvent event = new TimetableEvent();
            event.setId(id);
            event.setName(name);
            event.setDepartment(determineTimetableEventDepartment(
                studyProgramNames.getFirst()));
            event.setType(determineTimetableEventType(name));
            event.setYear(determineTimetableEventYear(studyProgramNames.getFirst()));
            event.setStartDateTime(utcStart);
            event.setEndDateTime(utcEnd);
            event.setStudyProgramIds(studyProgramIds);
            event.setClassRoomIds(classRoomIds);
            event.setTeacherIds(teacherIds);

            eventsByDayOfWeek.computeIfAbsent(dayOfWeek, k -> new ArrayList<>())
                .add(event);
        }

        Timetable timetable = new Timetable();
        timetable.setMonday(eventsByDayOfWeek.getOrDefault(DayOfWeek.MONDAY,
            new ArrayList<>()));
        timetable.setTuesday(eventsByDayOfWeek.getOrDefault(DayOfWeek.TUESDAY,
            new ArrayList<>()));
        timetable.setWednesday(eventsByDayOfWeek.getOrDefault(DayOfWeek.WEDNESDAY,
            new ArrayList<>()));
        timetable.setThursday(eventsByDayOfWeek.getOrDefault(DayOfWeek.THURSDAY,
            new ArrayList<>()));
        timetable.setFriday(eventsByDayOfWeek.getOrDefault(DayOfWeek.FRIDAY,
            new ArrayList<>()));
        timetable.setSaturday(eventsByDayOfWeek.getOrDefault(DayOfWeek.SATURDAY,
            new ArrayList<>()));
        timetable.setSunday(eventsByDayOfWeek.getOrDefault(DayOfWeek.SUNDAY,
            new ArrayList<>()));

        return timetable;
    }

    private List<Long> parseIds(JsonNode idsNode) {
        List<Long> ids = new ArrayList<>();
        if (idsNode.isArray()) {
            for (JsonNode idNode : idsNode) {
                if (idNode.asText().isEmpty()) {
                    continue;
                }
                ids.add(Long.parseLong(idNode.asText()));
            }
        }
        return ids;
    }

    private TimetableEvent.TimetableEventDepartment determineTimetableEventDepartment(
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
            return TimetableEvent.TimetableEventDepartment.COMPUTER_SCIENCE;
        }
    }

    private TimetableEvent.TimetableEventType determineTimetableEventType(String subject) {
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

    private TimetableEvent.TimetableEventYear determineTimetableEventYear(String studyProgramName) {
        var regex = ".*-(\\s*)?(\\d)\\.? ?(godina|god).*?$";
        switch (studyProgramName.replaceAll(regex, "$2")) {
            case "2" -> {
                return TimetableEvent.TimetableEventYear.SECOND;
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
                return TimetableEvent.TimetableEventYear.FIRST;
            }
        }
    }
}
