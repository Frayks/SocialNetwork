package andrew.project.socialNetwork.backend.api.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class WebSocketMessage {
    private String type;
}
