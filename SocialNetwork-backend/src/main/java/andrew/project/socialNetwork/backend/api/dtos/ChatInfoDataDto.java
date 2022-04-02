package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatInfoDataDto {
    private Long userId;
    private List<UserChatInfoDto> userChatInfoList;
}
