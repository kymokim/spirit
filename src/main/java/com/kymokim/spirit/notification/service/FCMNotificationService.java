package com.kymokim.spirit.notification.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.service.AuthResolver;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@NoArgsConstructor
public class FCMNotificationService {

    @PostConstruct
    public void init() {
        try {
            String FCM_PRIVATE_KEY_PATH = "/secret/drinktoday-d0f50-firebase-adminsdk-fbsvc-70665ba25a.json";
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                            GoogleCredentials
                                    .fromStream(new ClassPathResource(FCM_PRIVATE_KEY_PATH).getInputStream())
                                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform")))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase 연결 완료");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void pushAlarmToToken(com.kymokim.spirit.notification.entity.Notification notification) {
        Auth user = AuthResolver.resolveUser(notification.getUserId());
        if (Objects.equals(user.getFcmToken(), null) || user.getFcmToken().isEmpty()) {
            return;
        }
        Notification fcmNotification = Notification.builder()
                .setTitle(notification.getNotificationType().getTitle())
                .setBody(notification.getNotificationBody())
                .build();
        Message message = Message.builder()
                .setToken(user.getFcmToken())
                .setNotification(fcmNotification)
                .putData("notificationType", notification.getNotificationType().toString())
                .putData("notificationId", notification.getId().toString())
                .putData("redirectType", notification.getRedirectTarget().getRedirectType().toString())
                .putData("redirectId", notification.getRedirectTarget().getRedirectId().toString())
                .build();
        sendMessage(message);
        log.info("fcm content : {}", notification.getNotificationBody());
    }

    public void sendMessage(Message message) {
        try {
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();

            log.info("전송 성공 {}", response);
        } catch (ExecutionException | InterruptedException e) {
            log.info("실패 {}", e.getMessage());
        }
    }
}
