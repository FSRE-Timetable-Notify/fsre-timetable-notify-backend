package me.leopetrovic.fsretimetablenotify.messaging;

import me.leopetrovic.fsretimetablenotify.messaging.models.MessagingSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessagingRepository extends JpaRepository<MessagingSubscription, UUID> {
    List<MessagingSubscription> findByFcmToken(String fcmToken);

    List<MessagingSubscription> findByEmail(String email);

    List<MessagingSubscription> findByEmailAndFcmToken(
        String email,
        String fcmToken
    );

    List<MessagingSubscription> findByStudyProgramId(Long studyProgramId);
}