package me.leopetrovic.fsretimetablenotify.common.transformers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.leopetrovic.fsretimetablenotify.common.dto.FsreError;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class FsreErrorSerializer extends JsonSerializer<FsreError> {
    @Override
    public void serialize(
        FsreError fsreError,
        JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider
    ) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("status", fsreError.getStatus().value());
        jsonGenerator.writeStringField("error", fsreError.getError());
        jsonGenerator.writeStringField("message", fsreError.getMessage());

        if (fsreError.getDetails() != null) {
            jsonGenerator.writeStringField("details",
                fsreError.getDetails());
        }

        jsonGenerator.writeEndObject();
    }

}