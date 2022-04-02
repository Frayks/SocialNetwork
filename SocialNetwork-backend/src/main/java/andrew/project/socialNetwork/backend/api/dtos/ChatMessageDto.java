package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatMessageDto {
    private Long id;
    private Long chatId;
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String avatarUri;
    private String text;
    private String creationTime;
    private Boolean revised;

}
