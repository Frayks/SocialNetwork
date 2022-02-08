package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserFriendDto {
    private String username;
    private String firstName;
    private String lastName;
    private String avatarUrl;
}
