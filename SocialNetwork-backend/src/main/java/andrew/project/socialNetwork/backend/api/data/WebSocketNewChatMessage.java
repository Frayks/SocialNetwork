package andrew.project.socialNetwork.backend.api.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class WebSocketNewChatMessage extends WebSocketMessage {
    private NewChatMessage body;

}
