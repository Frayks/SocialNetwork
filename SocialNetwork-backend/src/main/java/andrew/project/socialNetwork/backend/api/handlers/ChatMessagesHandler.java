package andrew.project.socialNetwork.backend.api.handlers;

import andrew.project.socialNetwork.backend.api.data.WebSocketNewChatMessage;
import andrew.project.socialNetwork.backend.api.data.WebSocketViewedMessages;

public interface ChatMessagesHandler {
    void handleNewMessage(String sessionId, WebSocketNewChatMessage webSocketNewChatMessage);

    void handleViewedMessages(String id, WebSocketViewedMessages viewedMessagesData);

}
