package me.leopetrovic.fsretimetablenotify.messaging;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import me.leopetrovic.fsretimetablenotify.messaging.dto.TimetableUpdatedMessageDto;
import me.leopetrovic.fsretimetablenotify.messaging.exceptions.FcmSendMessageFailed;
import me.leopetrovic.fsretimetablenotify.messaging.exceptions.MessagingSubscriptionAlreadyRegisteredException;
import me.leopetrovic.fsretimetablenotify.messaging.models.MessagingSubscription;

@Service
public class MessagingService {
	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	MessagingRepository messagingRepository;

	public List<MessagingSubscription> getAll() {
		return messagingRepository.findAll();
	}

	public List<MessagingSubscription> getAllByStudyProgramId(Long studyProgramId) {
		return messagingRepository.findByStudyProgramId(studyProgramId);
	}

	public MessagingSubscription subscribe(String fcmToken, Long studyProgramId) {
		final List<MessagingSubscription> subscriptions = messagingRepository.findByFcmToken(fcmToken);

		if (subscriptions.stream().anyMatch(subscription -> subscription.getStudyProgramId().equals(studyProgramId))) {
			throw new MessagingSubscriptionAlreadyRegisteredException(fcmToken, studyProgramId);
		}

		final MessagingSubscription messagingSubscription = new MessagingSubscription();
		messagingSubscription.setFcmToken(fcmToken);
		messagingSubscription.setStudyProgramId(studyProgramId);

		return messagingRepository.save(messagingSubscription);
	}

	public void unsubscribe(String fcmToken, Long studyProgramId) {
		final List<MessagingSubscription> subscriptions = messagingRepository.findByFcmToken(fcmToken);

		subscriptions.stream().filter(subscription -> subscription.getStudyProgramId().equals(studyProgramId))
				.forEach(messagingRepository::delete);
	}

	public void sendMessage(String fcmToken, TimetableUpdatedMessageDto timetableUpdatedMessageDto) {
		try {
			Message message = Message.builder()
					.setToken(fcmToken)
					.putData("newTimetableEvents",
							objectMapper.writeValueAsString(timetableUpdatedMessageDto.difference().getNewEvents()))
					.putData("removedTimetableEvents",
							objectMapper.writeValueAsString(timetableUpdatedMessageDto.difference().getRemovedEvents()))
					.putData("timetableKey", objectMapper.writeValueAsString(timetableUpdatedMessageDto
							.timetableKey()))
					.putData("timestamp", LocalDateTime.now().toString())
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
}
