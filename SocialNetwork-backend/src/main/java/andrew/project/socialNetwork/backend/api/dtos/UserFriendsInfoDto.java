package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserFriendsInfoDto {
    private ShortUserInfoDto shortUserInfo;
    private List<UserFriendDto> userFriendList = new ArrayList<>();
    private List<UserFriendDto> userFriendRequestList = new ArrayList<>();
}
