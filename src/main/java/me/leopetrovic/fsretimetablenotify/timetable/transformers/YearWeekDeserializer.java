package me.leopetrovic.fsretimetablenotify.timetable.transformers;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;
import org.threeten.extra.YearWeek;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonDeserializer;

@JsonComponent
public class YearWeekDeserializer extends JsonDeserializer<YearWeek> {
	@Override
	public YearWeek deserialize(com.fasterxml.jackson.core.JsonParser p,
			com.fasterxml.jackson.databind.DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return YearWeek.parse(p.getText());
	}
}