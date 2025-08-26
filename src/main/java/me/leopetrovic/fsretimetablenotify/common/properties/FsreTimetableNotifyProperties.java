package me.leopetrovic.fsretimetablenotify.common.properties;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "fsre-timetable-notify")
public record FsreTimetableNotifyProperties(
    @NotBlank
    @URL
    URI timetableUri,
    @NotBlank
    @URL
    URI timetableDbUri
) {}
