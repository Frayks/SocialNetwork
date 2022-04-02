package andrew.project.socialNetwork.backend.handlers;

import andrew.project.socialNetwork.backend.api.constants.WebSocketMessageType;
import andrew.project.socialNetwork.backend.api.data.*;
import andrew.project.socialNetwork.backend.api.dtos.ChatMessageDto;
import andrew.project.socialNetwork.backend.api.entities.User;
import andrew.project.socialNetwork.backend.api.entities.UserChat;
import andrew.project.socialNetwork.backend.api.entities.UserChatMessage;
import andrew.project.socialNetwork.backend.api.entities.UserWsSession;
import andrew.project.socialNetwork.backend.api.handlers.ChatMessagesHandler;
import andrew.project.socialNetwork.backend.api.mappers.Mapper;
import andrew.project.socialNetwork.backend.api.properties.ImageStorageProperties;
import andrew.project.socialNetwork.backend.api.services.UserChatMessageService;
import andrew.project.socialNetwork.backend.api.services.UserChatService;
import andrew.project.socialNetwork.backend.api.services.UserService;
import andrew.project.socialNetwork.backend.api.services.UserWsSessionService;
import andrew.project.socialNetwork.backend.api.storages.WebSocketSessionsStorage;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ChatMessagesHandlerImpl implements ChatMessagesHandler {

    private static final Logger LOGGER = LogManager.getLogger(ChatMessagesHandlerImpl.class);

    private Gson gson = new Gson();

    private WebSocketSessionsStorage webSocketSessionsStorage;
    private UserWsSessionService userWsSessionService;
    private UserChatService userChatService;
    private UserChatMessageService userChatMessageService;
    private UserService userService;
    private ImageStorageProperties imageStorageProperties;

    @Override
    public void handleNewMessage(String sessionId, WebSocketNewChatMessage webSocketNewChatMessage) {
        try {
            NewChatMessage newChatMessage = webSocketNewChatMessage.getBody();
            if (newChatMessage != null && StringUtils.hasText(newChatMessage.getText())) {
                UserWsSession userWsSession = userWsSessionService.findBySessionId(sessionId);
                UserChat userChat = userChatService.findById(newChatMessage.getChatId());
                if (userChat != null && isChatMember(userChat, userWsSession.getUserId())) {
                    User owner = userService.findById(userWsSession.getUserId());
                    Long recipientId = getAnotherMember(userChat, owner.getId());

                    if (userChat.getFirstUserId().equals(recipientId)) {
                        userChat.setFirstUserNumOfUnreadMessages(userChat.getFirstUserNumOfUnreadMessages() + 1);
                    } else {
                        userChat.setSecondUserNumOfUnreadMessages(userChat.getSecondUserNumOfUnreadMessages() + 1);
                    }
                    userChatService.save(userChat);

                    UserChatMessage userChatMessage = Mapper.mapToUserChatMessage(userChat, owner, newChatMessage);
                    userChatMessage = userChatMessageService.setCreationTimeAndSave(userChatMessage);

                    ChatMessageDto chatMessage = Mapper.mapToChatMessageDto(userChatMessage, owner, imageStorageProperties);

                    List<WebSocketSession> ownerSessions = getUserWebSocketSessionList(owner.getId());
                    sendMessage(ownerSessions, chatMessage);

                    List<WebSocketSession> recipientSessions = getUserWebSocketSessionList(recipientId);
                    sendMessage(recipientSessions, chatMessage);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void handleViewedMessages(String sessionId, WebSocketViewedMessages webSocketViewedMessages) {
        try {
            ViewedMessagesData viewedMessagesData = webSocketViewedMessages.getBody();
            if (viewedMessagesData != null && !CollectionUtils.isEmpty(viewedMessagesData.getViewedMessagesIdsList())) {
                List<Long> viewedMessagesIdsList = viewedMessagesData.getViewedMessagesIdsList();
                UserWsSession userWsSession = userWsSessionService.findBySessionId(sessionId);
                UserChat userChat = userChatService.findById(viewedMessagesData.getChatId());
                if (userChat != null && isChatMember(userChat, userWsSession.getUserId())) {
                    User owner = userService.findById(userWsSession.getUserId());
                    Long recipientId = getAnotherMember(userChat, owner.getId());
                    List<UserChatMessage> viewedMessagesList = userChatMessageService.findByIdInAndChatIdAndUserIdAndRevised(viewedMessagesIdsList, userChat.getId(), recipientId, false);
                    if (viewedMessagesList.size() > 0) {
                        List<Long> confirmedViewedMessagesIdsList = new ArrayList<>();
                        for (UserChatMessage userChatMessage : viewedMessagesList) {
                            userChatMessage.setRevised(true);
                            confirmedViewedMessagesIdsList.add(userChatMessage.getId());
                        }
                        if (userChat.getFirstUserId().equals(owner.getId())) {
                            userChat.setFirstUserNumOfUnreadMessages(userChat.getFirstUserNumOfUnreadMessages() - viewedMessagesList.size());
                        } else {
                            userChat.setSecondUserNumOfUnreadMessages(userChat.getSecondUserNumOfUnreadMessages() - viewedMessagesList.size());
                        }
                        userChatService.save(userChat);
                        userChatMessageService.saveAll(viewedMessagesList);

                        ViewedMessagesData confirmedViewedMessagesData = new ViewedMessagesData();
                        confirmedViewedMessagesData.setChatId(userChat.getId());
                        confirmedViewedMessagesData.setViewedMessagesIdsList(confirmedViewedMessagesIdsList);

                        List<WebSocketSession> ownerSessions = getUserWebSocketSessionList(owner.getId());
                        sendInfoAboutViewedMessages(ownerSessions, confirmedViewedMessagesData);

                        List<WebSocketSession> recipientSessions = getUserWebSocketSessionList(recipientId);
                        sendInfoAboutViewedMessages(recipientSessions, confirmedViewedMessagesData);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private void sendInfoAboutViewedMessages(List<WebSocketSession> webSocketSessionList, ViewedMessagesData viewedMessagesData) {
        WebSocketViewedMessages webSocketViewedMessages = new WebSocketViewedMessages();
        webSocketViewedMessages.setType(WebSocketMessageType.VIEWED_MESSAGES.name());
        webSocketViewedMessages.setBody(viewedMessagesData);
        TextMessage textMessage = new TextMessage(gson.toJson(webSocketViewedMessages));
        sendTextMessage(webSocketSessionList, textMessage);
    }

    private void sendMessage(List<WebSocketSession> webSocketSessionList, ChatMessageDto chatMessage) {
        WebSocketChatMessage webSocketChatMessage = new WebSocketChatMessage();
        webSocketChatMessage.setType(WebSocketMessageType.MESSAGE.name());
        webSocketChatMessage.setBody(chatMessage);
        TextMessage textMessage = new TextMessage(gson.toJson(webSocketChatMessage));
        sendTextMessage(webSocketSessionList, textMessage);
    }

    private void sendTextMessage(List<WebSocketSession> webSocketSessionList, TextMessage textMessage) {
        for (WebSocketSession webSocketSession : webSocketSessionList) {
            try {
                webSocketSession.sendMessage(textMessage);
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
    }

    private List<WebSocketSession> getUserWebSocketSessionList(Long userId) {
        List<WebSocketSession> webSocketSessionList = new ArrayList<>();
        List<UserWsSession> userWsSessionList = userWsSessionService.findByUserId(userId);
        for (UserWsSession userWsSession : userWsSessionList) {
            WebSocketSession webSocketSession = webSocketSessionsStorage.get(userWsSession.getSessionId());
            if (webSocketSession != null) {
                webSocketSessionList.add(webSocketSession);
            }
        }
        return webSocketSessionList;
    }

    private Long getAnotherMember(UserChat userChat, Long userId) {
        return Objects.equals(userChat.getFirstUserId(), userId) ? userChat.getSecondUserId() : userChat.getFirstUserId();
    }

    private boolean isChatMember(UserChat userChat, Long userId) {
        return Objects.equals(userChat.getFirstUserId(), userId) || Objects.equals(userChat.getSecondUserId(), userId);
    }

    @Autowired
    public void setWebSocketSessionsStorage(WebSocketSessionsStorage webSocketSessionsStorage) {
        this.webSocketSessionsStorage = webSocketSessionsStorage;
    }

    @Autowired
    public void setUserWsSessionService(UserWsSessionService userWsSessionService) {
        this.userWsSessionService = userWsSessionService;
    }

    @Autowired
    public void setUserChatService(UserChatService userChatService) {
        this.userChatService = userChatService;
    }

    @Autowired
    public void setUserChatMessageService(UserChatMessageService userChatMessageService) {
        this.userChatMessageService = userChatMessageService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setImageStorageProperties(ImageStorageProperties imageStorageProperties) {
        this.imageStorageProperties = imageStorageProperties;
    }
}
