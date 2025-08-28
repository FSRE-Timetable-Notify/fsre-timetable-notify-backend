package me.leopetrovic.fsretimetablenotify.timetabledatabase.transformers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableEvent;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.StudyProgram;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.TimetableDatabase;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.TimetableDatabase.IdNamePair;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonComponent
public class TimetableDatabaseDeserializer extends JsonDeserializer<TimetableDatabase> {

    @Override
    public TimetableDatabase deserialize(
        JsonParser p,
        DeserializationContext ctx
    ) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        JsonNode tablesNode = node.get("r").get("tables");

        JsonNode teachersTable = tablesNode.get(0).get("data_rows");
        JsonNode subjectsTable = tablesNode.get(1).get("data_rows");
        JsonNode classroomsTable = tablesNode.get(2).get("data_rows");
        JsonNode classesTable = tablesNode.get(3).get("data_rows");
        JsonNode eventTypesTable = tablesNode.get(4).get("data_rows");

        var studyProgramPairs = parseIdNamePairs(classesTable, true, false);
        var studyPrograms = studyProgramPairs.stream()
            .map(pair -> new StudyProgram(pair.getId(),
                pair.getName(),
                determineTimetableEventYear(pair.getName()),
                determineTimetableEventDepartment(pair.getName())))
            .toList();

        TimetableDatabase database = new TimetableDatabase();
        database.setStudyPrograms(studyPrograms);
        database.setClassRooms(parseIdNamePairs(classroomsTable, true, true));
        database.setEventTypes(parseIdNamePairs(eventTypesTable, false));
        database.setSubjects(parseIdNamePairs(subjectsTable, true, false));
        database.setTeachers(parseIdNamePairs(teachersTable, true, true));

        return database;
    }

    private List<IdNamePair<Long>> parseIdNamePairs(
        JsonNode table,
        boolean parseIdAsLong,
        boolean useShortName
    ) {
        List<IdNamePair<Long>> pairs = new ArrayList<>();
        for (JsonNode row : table) {
            IdNamePair<Long> pair = new IdNamePair<>();
            pair.setId(row.get("id").asLong());
            pair.setName(row.get(useShortName ? "short" : "name").asText());
            pairs.add(pair);
        }
        return pairs;
    }

    private List<IdNamePair<String>> parseIdNamePairs(
        JsonNode table,
        boolean useShortName
    ) {
        List<IdNamePair<String>> pairs = new ArrayList<>();
        for (JsonNode row : table) {
            IdNamePair<String> pair = new IdNamePair<>();
            pair.setId(row.get("id").asText());
            pair.setName(row.get(useShortName ? "short" : "name").asText());
            pairs.add(pair);
        }
        return pairs;
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
