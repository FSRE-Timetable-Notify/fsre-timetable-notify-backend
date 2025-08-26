package me.leopetrovic.fsretimetablenotify.timetable.transformers;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;
import org.threeten.extra.YearWeek;

import java.io.IOException;

@JsonComponent
public class YearWeekDeserializer extends JsonDeserializer<YearWeek> {
    @Override
    public YearWeek deserialize(
        com.fasterxml.jackson.core.JsonParser p,
        com.fasterxml.jackson.databind.DeserializationContext ctxt
    ) throws IOException {
        return YearWeek.parse(p.getText());
    }
}