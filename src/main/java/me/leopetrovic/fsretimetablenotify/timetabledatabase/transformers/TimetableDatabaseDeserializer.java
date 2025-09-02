package me.leopetrovic.fsretimetablenotify.timetabledatabase.transformers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import me.leopetrovic.fsretimetablenotify.common.service.ParsingService;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.IdNamePair;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.StudyProgram;
import me.leopetrovic.fsretimetablenotify.timetabledatabase.models.TimetableDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonComponent
public class TimetableDatabaseDeserializer extends JsonDeserializer<TimetableDatabase> {
    private final ParsingService parsingService;

    @Autowired
    public TimetableDatabaseDeserializer(
        ParsingService parsingService
    ) {
        this.parsingService = parsingService;
    }

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
                parsingService.determineTimetableEventYear(pair.getName()),
                parsingService.determineTimetableEventDepartment(pair.getName()),
                parsingService.determineTimetableEventDirections(List.of(pair.getName())).getFirst()))
            .filter(sp -> {
                if (sp.studyYear() != null && sp.department() != null) {
                    return true;
                } else {
                    System.err.println("Warning: Could not parse study " + "program: " + sp.name() + " (reason: " + (
                        sp.studyYear() == null
                            ? "year "
                            : "") + (sp.department() == null
                        ? "department"
                        : "") + " is null)");
                    return false;
                }
            })
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


}
