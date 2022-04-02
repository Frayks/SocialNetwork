package andrew.project.socialNetwork.backend.api.data;

import andrew.project.socialNetwork.backend.api.dtos.ChatMessageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class WebSocketChatMessage extends WebSocketMessage {
    private ChatMessageDto body;

}
