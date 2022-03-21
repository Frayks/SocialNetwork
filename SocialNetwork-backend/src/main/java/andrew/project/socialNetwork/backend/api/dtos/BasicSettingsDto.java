package andrew.project.socialNetwork.backend.api.dtos;

import andrew.project.socialNetwork.backend.api.constants.Sex;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BasicSettingsDto {
    private String avatarUri;
    private String firstName;
    private String lastName;
    private String username;
    private int dayOfBirth;
    private int monthOfBirth;
    private int yearOfBirth;
    private String sex;

}
