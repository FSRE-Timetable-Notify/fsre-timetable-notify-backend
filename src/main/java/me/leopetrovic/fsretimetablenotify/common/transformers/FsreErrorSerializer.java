package me.leopetrovic.fsretimetablenotify.common.transformers;

import java.io.IOException;

import org.springframework.boot.jackson.JsonComponent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import me.leopetrovic.fsretimetablenotify.common.dto.FsreError;

@JsonComponent
public class FsreErrorSerializer extends JsonSerializer<FsreError> {
	@Override
	public void serialize(
			FsreError fsreError, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException, JsonProcessingException {
		jsonGenerator.writeStartObject();
		jsonGenerator.writeNumberField("status", fsreError.getStatus().value());
		jsonGenerator.writeStringField("error", fsreError.getError());
		jsonGenerator.writeStringField("message", fsreError.getMessage());

		if (fsreError.getDetails().isPresent()) {
			jsonGenerator.writeStringField("details", fsreError.getDetails().get());
		}

		jsonGenerator.writeEndObject();
	}

}