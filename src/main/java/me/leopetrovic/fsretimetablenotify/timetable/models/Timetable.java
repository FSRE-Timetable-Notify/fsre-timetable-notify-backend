package me.leopetrovic.fsretimetablenotify.timetable.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "The timetable for a week")
public class Timetable {
    @Schema(
        description = "The event list for Monday",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<TimetableEvent> monday;

    @Schema(
        description = "The event list for Tuesday",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<TimetableEvent> tuesday;

    @Schema(
        description = "The event list for Wednesday",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<TimetableEvent> wednesday;

    @Schema(
        description = "The event list for Thursday",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<TimetableEvent> thursday;

    @Schema(
        description = "The event list for Friday",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<TimetableEvent> friday;

    @Schema(
        description = "The event list for Saturday",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<TimetableEvent> saturday;

    @Schema(
        description = "The event list for Sunday",
        requiredMode = RequiredMode.REQUIRED
    )
    private List<TimetableEvent> sunday;

    @JsonIgnore
    public List<List<TimetableEvent>> getWeekDays() {
        return List.of(monday,
            tuesday,
            wednesday,
            thursday,
            friday,
            saturday,
            sunday);
    }

    public void merge(Timetable other) {
        if (other == null) {
            return;
        }

        if (other.monday != null) {
            if (this.monday == null) {
                this.monday = other.monday;
            } else {
                this.monday.addAll(other.monday);
            }
        }

        if (other.tuesday != null) {
            if (this.tuesday == null) {
                this.tuesday = other.tuesday;
            } else {
                this.tuesday.addAll(other.tuesday);
            }
        }

        if (other.wednesday != null) {
            if (this.wednesday == null) {
                this.wednesday = other.wednesday;
            } else {
                this.wednesday.addAll(other.wednesday);
            }
        }

        if (other.thursday != null) {
            if (this.thursday == null) {
                this.thursday = other.thursday;
            } else {
                this.thursday.addAll(other.thursday);
            }
        }

        if (other.friday != null) {
            if (this.friday == null) {
                this.friday = other.friday;
            } else {
                this.friday.addAll(other.friday);
            }
        }

        if (other.saturday != null) {
            if (this.saturday == null) {
                this.saturday = other.saturday;
            } else {
                this.saturday.addAll(other.saturday);
            }
        }

        if (other.sunday != null) {
            if (this.sunday == null) {
                this.sunday = other.sunday;
            } else {
                this.sunday.addAll(other.sunday);
            }
        }
    }

    @Override
    public String toString() {
        return "Timetable{" + "monday=" + monday + ", tuesday=" + tuesday + ", wednesday=" + wednesday + ", thursday=" + thursday + ", friday=" + friday + ", saturday=" + saturday + ", sunday=" + sunday + '}';
    }
}