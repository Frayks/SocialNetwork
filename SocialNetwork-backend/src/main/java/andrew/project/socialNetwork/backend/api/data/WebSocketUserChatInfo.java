package andrew.project.socialNetwork.backend.api.data;

import andrew.project.socialNetwork.backend.api.dtos.UserChatInfoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class WebSocketUserChatInfo extends WebSocketMessage {
    private UserChatInfoDto body;

}
