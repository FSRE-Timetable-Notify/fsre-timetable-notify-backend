package me.leopetrovic.fsretimetablenotify.timetabledatabase.transformers;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import me.leopetrovic.fsretimetablenotify.timetabledatabase.dto.ExternalFsreTimetableDatabaseRequest;

@JsonComponent
public class ExternalTimetableDatabaseRequestSerializer extends JsonSerializer<ExternalFsreTimetableDatabaseRequest> {
	@Override
	public void serialize(
			ExternalFsreTimetableDatabaseRequest value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException {
		gen.writeStartObject();
		gen.writeArrayFieldStart("__args");
		gen.writeNull();
		gen.writeNumber(value.year().minusYears(1).getValue());

		gen.writeStartObject(); // vt_filter
		gen.writeEndObject();

		gen.writeStartObject();
		gen.writeObjectFieldStart("needed_part");

		gen.writeArrayFieldStart("classes");
		gen.writeString("short");
		gen.writeString("name");
		gen.writeEndArray();

		gen.writeArrayFieldStart("classrooms");
		gen.writeString("short");
		gen.writeEndArray();

		gen.writeArrayFieldStart("event_types");
		gen.writeString("name");
		gen.writeEndArray();

		gen.writeArrayFieldStart("subjects");
		gen.writeString("short");
		gen.writeString("name");
		gen.writeEndArray();

		gen.writeArrayFieldStart("teachers");
		gen.writeString("short");
		gen.writeEndArray();

		gen.writeEndObject(); // end of needed_part
		gen.writeStringField("op", "fetch");
		gen.writeEndObject(); // end of fourth object in __args

		gen.writeEndArray(); // end of __args
		gen.writeStringField("__gsh", "00000000");
		gen.writeEndObject();
	}
}