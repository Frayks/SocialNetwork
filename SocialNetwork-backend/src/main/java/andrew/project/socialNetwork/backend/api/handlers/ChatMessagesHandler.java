package andrew.project.socialNetwork.backend.api.handlers;

import andrew.project.socialNetwork.backend.api.data.ViewedMessagesData;
import andrew.project.socialNetwork.backend.api.data.WebSocketNewChatMessage;
import andrew.project.socialNetwork.backend.api.data.WebSocketViewedMessages;
import andrew.project.socialNetwork.backend.api.dtos.ChatMessageDto;
import andrew.project.socialNetwork.backend.api.dtos.UserChatInfoDto;

public interface ChatMessagesHandler {
    void handleNewMessage(String sessionId, WebSocketNewChatMessage webSocketNewChatMessage);

    void handleViewedMessages(String id, WebSocketViewedMessages viewedMessagesData);

    void sendInfoAboutNewChat(Long userId, UserChatInfoDto userChatInfoDto);

    void sendInfoAboutViewedMessages(Long userId, ViewedMessagesData viewedMessagesData);

    void sendMessage(Long userId, ChatMessageDto chatMessage);
}
