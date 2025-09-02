package me.leopetrovic.fsretimetablenotify.timetabledatabase.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "A pair of an ID and a name, used in the timetable database")
public class IdNamePair<T> {
    @Schema(
        description = "The ID of the item",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private T id;
    @Schema(
        description = "The name of the item",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;
}
