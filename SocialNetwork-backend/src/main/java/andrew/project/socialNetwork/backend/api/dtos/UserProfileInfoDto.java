package andrew.project.socialNetwork.backend.api.dtos;

import andrew.project.socialNetwork.backend.api.constants.Sex;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserProfileInfoDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String avatarUri;
    private Date dateOfBirth;
    private Sex sex;
    private String city;
    private String school;
    private String university;
    private String aboutYourself;
    private int numOfPosts;
    private List<UserPhotoDto> userPhotoList = new ArrayList<>();
    private List<ShortUserInfoDto> userFriendList = new ArrayList<>();
    private boolean friend;
    private boolean requestToFriends;

}
