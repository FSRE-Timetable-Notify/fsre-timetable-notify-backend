package me.leopetrovic.fsretimetablenotify.messaging;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import me.leopetrovic.fsretimetablenotify.messaging.dto.TimetableUpdatedMessageDto;
import me.leopetrovic.fsretimetablenotify.messaging.exceptions.EmailSendMessageFailed;
import me.leopetrovic.fsretimetablenotify.messaging.exceptions.FcmSendMessageFailed;
import me.leopetrovic.fsretimetablenotify.messaging.exceptions.MessagingSubscriptionAlreadyRegisteredException;
import me.leopetrovic.fsretimetablenotify.messaging.models.MessagingSubscription;
import me.leopetrovic.fsretimetablenotify.timetable.models.TimetableEvent;

@Service
public class MessagingService {
	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	MessagingRepository messagingRepository;

	@Autowired
	JavaMailSender mailSender;

	public List<MessagingSubscription> getAll() {
		return messagingRepository.findAll();
	}

	public List<MessagingSubscription> getAllByStudyProgramId(Long studyProgramId) {
		return messagingRepository.findByStudyProgramId(studyProgramId);
	}

	public MessagingSubscription subscribe(String email, String fcmToken, Long studyProgramId) {
		final List<MessagingSubscription> subscriptions = messagingRepository.findByEmailAndFcmToken(email, fcmToken);

		if (subscriptions.stream().anyMatch(subscription -> subscription.getStudyProgramId().equals(studyProgramId))) {
			throw new MessagingSubscriptionAlreadyRegisteredException(email, fcmToken, studyProgramId);
		}

		final MessagingSubscription messagingSubscription = new MessagingSubscription();
		messagingSubscription.setEmail(email);
		messagingSubscription.setFcmToken(fcmToken);
		messagingSubscription.setStudyProgramId(studyProgramId);

		return messagingRepository.save(messagingSubscription);
	}

	public void unsubscribe(String email, String fcmToken, Long studyProgramId) {
		final List<MessagingSubscription> subscriptions = messagingRepository.findByEmailAndFcmToken(email, fcmToken);

		subscriptions.stream().filter(subscription -> subscription.getStudyProgramId().equals(studyProgramId))
				.forEach(messagingRepository::delete);
	}

	public void sendMessage(MessagingSubscription messagingSubscription,
			TimetableUpdatedMessageDto timetableUpdatedMessageDto) {
		if (messagingSubscription.getFcmToken().isPresent()) {
			var fcmToken = messagingSubscription.getFcmToken().get();
			sendFirebaseMessage(fcmToken, timetableUpdatedMessageDto);
		} else if (messagingSubscription.getEmail().isPresent()) {
			var email = messagingSubscription.getEmail().get();
			sendEmailMessage(email, timetableUpdatedMessageDto);
		}
	}

	private void sendFirebaseMessage(String fcmToken, TimetableUpdatedMessageDto timetableUpdatedMessageDto) {
		try {
			Message message = Message.builder()
					.setToken(
							fcmToken)
					.putData("newTimetableEvents",
							objectMapper.writeValueAsString(timetableUpdatedMessageDto.difference().getNewEvents()))
					.putData("removedTimetableEvents",
							objectMapper.writeValueAsString(timetableUpdatedMessageDto.difference().getRemovedEvents()))
					.putData("timetableKey", objectMapper.writeValueAsString(timetableUpdatedMessageDto
							.timetableKey()))
					.putData("timestamp", Instant.now().toString())
					.setAndroidConfig(AndroidConfig.builder().setPriority(
							com.google.firebase.messaging.AndroidConfig.Priority.HIGH)
							.setNotification(AndroidNotification.builder().setPriority(
									com.google.firebase.messaging.AndroidNotification.Priority.HIGH).build())
							.build())
					.setNotification(Notification.builder().setTitle("Timetable updated")
							.setBody("Your timetable has been updated").build())
					.build();

			FirebaseMessaging.getInstance().send(message);
		} catch (JsonProcessingException e) {
			throw new FcmSendMessageFailed("Failed to serialize timetable events to JSON", e);
		} catch (FirebaseMessagingException e) {
			throw new FcmSendMessageFailed("Failed to send message using FCM", e);
		}
	}

	private void sendEmailMessage(String email, TimetableUpdatedMessageDto timetableUpdatedMessageDto) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);

			String content = "<h1>Timetable updated</h1>"
					+ "<p>Your timetable has been updated. The following changes have been made:</p>"
					+ "<h4>New events:</h4>"
					+ "<ul>";

			for (var event : timetableUpdatedMessageDto.difference().getNewEvents()) {
				content += formatEvent(event, true);
			}

			content += "</ul><h4>Removed events:</h4><ul>";

			for (var event : timetableUpdatedMessageDto.difference().getRemovedEvents()) {
				content += formatEvent(event, false);
			}

			content += "</ul>";

			helper.setSubject("Timetable updated");
			helper.setFrom("fsrenotifier@gmail.com", "FSRE Notifier");
			helper.setTo(email);
			helper.setText(content, true);

			mailSender.send(message);
		} catch (UnsupportedEncodingException e) {
			throw new EmailSendMessageFailed("Failed to set from address", e);
		} catch (MessagingException e) {
			throw new EmailSendMessageFailed("Failed to create MimeMessageHelper", e);
		} catch (MailException e) {
			throw new EmailSendMessageFailed("Failed to send email", e);
		}
	}

	private String formatEvent(TimetableEvent event, boolean isNew) {
		var content = "";

		var eventName = event.getName();
		var startDate = event.getStartDate();
		var endDate = event.getEndDate();
		var teacherNames = event.getTeacherNames();
		var classRoomNames = event.getClassRoomNames();
		var studyProgramNames = event.getStudyProgramNames();
		var formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC);

		content += "<li style=\"background-color: rgba(" + (isNew ? "0, 255" : "255, 0")
				+ ", 0, 0.1); padding: 1rem;\"><strong>" + eventName + "</strong><ul>";
		content += "<li>Duration: " + formatter.format(startDate) + " - " + formatter.format(endDate) + "</li>";

		if (teacherNames.size() == 1) {
			content += "<li>Teacher: " + teacherNames.get(0) + "</li>";
		} else {
			content += "<li>With teachers: " + String.join(", ", teacherNames) + "</li>";
		}
		if (classRoomNames.size() == 1) {
			content += "<li>Class room: " + classRoomNames.get(0) + "</li>";
		} else {
			content += "<li>In class rooms: " + String.join(", ", classRoomNames) + "</li>";
		}
		if (studyProgramNames.size() == 1) {
			content += "<li> Study program: " + studyProgramNames.get(0) + "</li>";
		} else {
			content += "<li>For study programs: " + String.join(", ", studyProgramNames) + "</li>";
		}

		content += "</ul></li>";

		return content;
	}
}
