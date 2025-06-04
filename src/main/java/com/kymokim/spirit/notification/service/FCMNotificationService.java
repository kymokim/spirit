package com.kymokim.spirit.notification.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.kymokim.spirit.notification.entity.NotificationType;
import com.kymokim.spirit.notification.entity.RedirectTarget;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
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

    public void pushAlarmToToken(NotificationType notificationType, String body, String token, RedirectTarget redirectTarget) {
        if (token == null || token.isEmpty()) {
            return;
        }
        Notification notification = Notification.builder()
                .setTitle(notificationType.getTitle())
                .setBody(body)
                .build();
        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .putData("notificationType", notificationType.toString())
                .putData("redirectType", redirectTarget.getRedirectType().toString())
                .putData("redirectId", redirectTarget.getRedirectId().toString())
                .build();
        sendMessage(message);
        log.info("fcm content : {}", body);
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
