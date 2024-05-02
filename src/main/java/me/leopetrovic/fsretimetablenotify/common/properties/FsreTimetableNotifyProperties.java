package me.leopetrovic.fsretimetablenotify.common.properties;

import java.net.URI;

import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "fsre-timetable-notify")
public record FsreTimetableNotifyProperties(@NotBlank @URL URI timetableUri, @NotBlank @URL URI timetableDbUri) {
}
