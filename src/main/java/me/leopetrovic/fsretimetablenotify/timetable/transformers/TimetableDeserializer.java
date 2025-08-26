package me.leopetrovic.fsretimetablenotify.timetable.transformers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import me.leopetrovic.fsretimetablenotify.timetable.models.Timetable;
import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableEvent;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@JsonComponent
public class TimetableDeserializer extends JsonDeserializer<Timetable> {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(
        "HH:mm");

    @Override
    public Timetable deserialize(
        JsonParser p,
        DeserializationContext ctxt
    ) throws IOException, JacksonException {
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
            Instant start = date.atTime(startTime)
                .atZone(ZoneId.systemDefault())
                .toInstant();

            String endTimeStr = itemNode.get("endtime").asText();
            LocalTime endTime = LocalTime.parse(endTimeStr, TIME_FORMATTER);
            Instant end = date.atTime(endTime)
                .atZone(ZoneId.systemDefault())
                .toInstant();

            String name = itemNode.get("name").asText();

            Long id = Long.parseLong(itemNode.get("subjectid").asText());

            List<Long> studyProgramIds = parseIds(itemNode.get("classids"));
            List<Long> classRoomIds = parseIds(itemNode.get("classroomids"));
            List<Long> teacherIds = parseIds(itemNode.get("teacherids"));

            TimetableEvent event = new TimetableEvent();
            event.setName(name);
            event.setStartDate(start);
            event.setEndDate(end);
            event.setId(id);
            event.setStudyProgramIds(studyProgramIds);
            event.setClassRoomIds(classRoomIds);
            event.setTeacherIds(teacherIds);

            eventsByDayOfWeek.computeIfAbsent(dayOfWeek, k -> new ArrayList<>())
                .add(event);
        }

        Timetable timetable = new Timetable();
        timetable.setMonday(eventsByDayOfWeek.getOrDefault(DayOfWeek.MONDAY,
            Collections.emptyList()));
        timetable.setTuesday(eventsByDayOfWeek.getOrDefault(DayOfWeek.TUESDAY,
            Collections.emptyList()));
        timetable.setWednesday(eventsByDayOfWeek.getOrDefault(DayOfWeek.WEDNESDAY,
            Collections.emptyList()));
        timetable.setThursday(eventsByDayOfWeek.getOrDefault(DayOfWeek.THURSDAY,
            Collections.emptyList()));
        timetable.setFriday(eventsByDayOfWeek.getOrDefault(DayOfWeek.FRIDAY,
            Collections.emptyList()));
        timetable.setSaturday(eventsByDayOfWeek.getOrDefault(DayOfWeek.SATURDAY,
            Collections.emptyList()));
        timetable.setSunday(eventsByDayOfWeek.getOrDefault(DayOfWeek.SUNDAY,
            Collections.emptyList()));

        return timetable;
    }

    private List<Long> parseIds(JsonNode idsNode) {
        List<Long> ids = new ArrayList<>();
        if (idsNode.isArray()) {
            for (JsonNode idNode : idsNode) {
                ids.add(Long.parseLong(idNode.asText()));
            }
        }
        return ids;
    }
}
