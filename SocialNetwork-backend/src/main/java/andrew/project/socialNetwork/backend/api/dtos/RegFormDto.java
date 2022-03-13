package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RegFormDto {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private int dayOfBirth;
    private int monthOfBirth;
    private int yearOfBirth;
    private String sex;

}
