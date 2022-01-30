package andrew.project.socialNetwork.backend.api.dtos;

import andrew.project.socialNetwork.backend.api.constants.Sex;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserProfileInfoDto {

    private String username;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Date dateOfBirth;
    private Sex sex;
    private String city;
    private String school;
    private String university;
    private String aboutYourself;
    private List<UserPhotoDto> userPhotoList = new ArrayList<>();
    private List<UserPostDto> userPostList = new ArrayList<>();

}
