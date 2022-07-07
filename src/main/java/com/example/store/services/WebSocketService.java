package com.example.store.services;

import com.example.store.model.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessageSendingOperations messagingTemplate;
    private static final String NOTIFICATION_TOPIC = "/ws/topic/notification/";
//    private static final String MESSAGE_TOPIC = "/ws/topic/message/";
//    private static final String UNREAD_COUNT_TOPIC = "/ws/topic/message/unread/";

    public void sendNotification(NotificationDTO notificationDTO, int receiverId) {
        messagingTemplate.convertAndSend(NOTIFICATION_TOPIC + receiverId, notificationDTO);
    }

//    public void sendMessage(int receiverId, MessageDTO messageDTO) {
//        messagingTemplate.convertAndSend(MESSAGE_TOPIC + receiverId, messageDTO);
//    }
//
//    public void sendUnreadCount(int receiverId) {
//        messagingTemplate.convertAndSend(UNREAD_COUNT_TOPIC + receiverId, "ok");
//    }
}
