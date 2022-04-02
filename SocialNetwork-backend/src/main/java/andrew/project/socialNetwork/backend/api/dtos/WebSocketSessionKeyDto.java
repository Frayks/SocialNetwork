package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class WebSocketSessionKeyDto {
    private String key;

    public WebSocketSessionKeyDto(String key) {
        this.key = key;
    }

}
