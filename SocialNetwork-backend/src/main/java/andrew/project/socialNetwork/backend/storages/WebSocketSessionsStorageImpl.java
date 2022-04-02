package andrew.project.socialNetwork.backend.storages;

import andrew.project.socialNetwork.backend.api.storages.WebSocketSessionsStorage;
import andrew.project.socialNetwork.backend.api.utils.GeneratorUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class WebSocketSessionsStorageImpl implements WebSocketSessionsStorage {

    private static final String KEY = "key";

    private Map<String, WebSocketSession> webSocketSessionMap = new HashMap<>();
    private Map<String, Long> webSocketRequestMap = new HashMap<>();

    @Override
    public Long validateAndPut(WebSocketSession webSocketSession) throws Exception {
        List<NameValuePair> nameValuePairList = URLEncodedUtils.parse(webSocketSession.getUri(), StandardCharsets.UTF_8);
        String key = null;
        for (NameValuePair nameValuePair : nameValuePairList) {
            if (nameValuePair.getName().equals(KEY)) {
                key = nameValuePair.getValue();
                break;
            }
        }
        if (!StringUtils.hasText(key)) {
            throw new Exception("Websocket tries to establish a connection without an access token!");
        }
        Long userId = webSocketRequestMap.get(key);
        if (Objects.nonNull(userId)) {
            webSocketRequestMap.remove(key);
            webSocketSessionMap.put(webSocketSession.getId(), webSocketSession);
        }
        return userId;
    }

    @Override
    public String generateWebSocketRequestKey(Long userId) {
        String key = GeneratorUtil.genRandStr(GeneratorUtil.SHORT_KEY_LENGTH);
        webSocketRequestMap.put(key, userId);
        return key;
    }

    @Override
    public WebSocketSession get(String webSocketSessionId) {
        return webSocketSessionMap.get(webSocketSessionId);
    }

    @Override
    public WebSocketSession remove(String webSocketSessionId) {
        return webSocketSessionMap.remove(webSocketSessionId);
    }

}