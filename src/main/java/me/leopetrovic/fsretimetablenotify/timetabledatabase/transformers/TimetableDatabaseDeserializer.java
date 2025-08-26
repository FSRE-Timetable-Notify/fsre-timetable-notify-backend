package me.leopetrovic.fsretimetablenotify.timetabledatabase.transformers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
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
        DeserializationContext ctxt
    ) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);
        JsonNode tablesNode = node.get("r").get("tables");

        JsonNode teachersTable = tablesNode.get(0).get("data_rows");
        JsonNode subjectsTable = tablesNode.get(1).get("data_rows");
        JsonNode classroomsTable = tablesNode.get(2).get("data_rows");
        JsonNode classesTable = tablesNode.get(3).get("data_rows");
        JsonNode eventTypesTable = tablesNode.get(4).get("data_rows");

        TimetableDatabase database = new TimetableDatabase();
        database.setStudyPrograms(parseIdNamePairs(classesTable, true, false));
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
