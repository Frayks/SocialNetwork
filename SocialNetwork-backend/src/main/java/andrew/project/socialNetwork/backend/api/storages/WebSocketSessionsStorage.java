package andrew.project.socialNetwork.backend.api.storages;

import org.springframework.web.socket.WebSocketSession;

public interface WebSocketSessionsStorage {

    Long validateAndPut(WebSocketSession webSocketSession) throws Exception;

    String generateWebSocketRequestKey(Long userId);

    WebSocketSession get(String webSocketSessionId);

    WebSocketSession remove(String webSocketSessionId);

}