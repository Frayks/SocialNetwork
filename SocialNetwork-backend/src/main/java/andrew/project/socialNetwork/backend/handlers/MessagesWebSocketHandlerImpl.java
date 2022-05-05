package andrew.project.socialNetwork.backend.handlers;

import andrew.project.socialNetwork.backend.api.constants.TokenType;
import andrew.project.socialNetwork.backend.api.constants.WebSocketMessageType;
import andrew.project.socialNetwork.backend.api.data.WebSocketMessage;
import andrew.project.socialNetwork.backend.api.data.WebSocketNewChatMessage;
import andrew.project.socialNetwork.backend.api.data.WebSocketViewedMessages;
import andrew.project.socialNetwork.backend.api.entities.UserWsSession;
import andrew.project.socialNetwork.backend.api.handlers.ChatMessagesHandler;
import andrew.project.socialNetwork.backend.api.services.UserService;
import andrew.project.socialNetwork.backend.api.services.UserWsSessionService;
import andrew.project.socialNetwork.backend.api.storages.WebSocketSessionsStorage;
import andrew.project.socialNetwork.backend.security.JwtProvider;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Objects;

@Component
public class MessagesWebSocketHandlerImpl extends TextWebSocketHandler {

    private static final Logger LOGGER = LogManager.getLogger(MessagesWebSocketHandlerImpl.class);

    private Gson gson = new Gson();

    private WebSocketSessionsStorage webSocketSessionsStorage;
    private ChatMessagesHandler chatMessagesHandler;
    private UserWsSessionService userWsSessionService;
    private UserService userService;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            Long userId = webSocketSessionsStorage.validateAndPut(session);
            if (Objects.isNull(userId)) {
                throw new Exception("WebSocketSession has wrong key!");
            }
            andrew.project.socialNetwork.backend.api.entities.User userDbi = userService.findById(userId);
            UserWsSession userWsSession = new UserWsSession();
            userWsSession.setUserId(userDbi.getId());
            userWsSession.setSessionId(session.getId());
            userWsSessionService.save(userWsSession);
        } catch (Exception e) {
            LOGGER.debug(e);
            try {
                session.close(CloseStatus.NOT_ACCEPTABLE);
            } catch (IOException ex) {
                LOGGER.info(ex);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            handleWebSocketMessageType(session, message);
        } catch (JsonSyntaxException e) {
            LOGGER.error(e);
        }
    }

    private void handleWebSocketMessageType(WebSocketSession session, TextMessage message) throws JsonSyntaxException {
        String payload = message.getPayload();
        WebSocketMessage webSocketMessage = gson.fromJson(payload, WebSocketMessage.class);
        if (webSocketMessage.getType().equals(WebSocketMessageType.MESSAGE.name())) {
            WebSocketNewChatMessage webSocketNewChatMessage = gson.fromJson(payload, WebSocketNewChatMessage.class);
            chatMessagesHandler.handleNewMessage(session.getId(), webSocketNewChatMessage);
        } else if (webSocketMessage.getType().equals(WebSocketMessageType.VIEWED_MESSAGES.name())) {
            WebSocketViewedMessages webSocketViewedMessages = gson.fromJson(payload, WebSocketViewedMessages.class);
            chatMessagesHandler.handleViewedMessages(session.getId(), webSocketViewedMessages);
        } else {
            LOGGER.error("Unknown message type: " + webSocketMessage.getType());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        userWsSessionService.deleteBySessionId(session.getId());
        webSocketSessionsStorage.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        userWsSessionService.deleteBySessionId(session.getId());
        webSocketSessionsStorage.remove(session.getId());
    }

    @Autowired
    public void setWebSocketSessionsStorage(WebSocketSessionsStorage webSocketSessionsStorage) {
        this.webSocketSessionsStorage = webSocketSessionsStorage;
    }

    @Autowired
    public void setChatMessagesHandler(ChatMessagesHandler chatMessagesHandler) {
        this.chatMessagesHandler = chatMessagesHandler;
    }

    @Autowired
    public void setUserWsSessionService(UserWsSessionService userWsSessionService) {
        this.userWsSessionService = userWsSessionService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

}