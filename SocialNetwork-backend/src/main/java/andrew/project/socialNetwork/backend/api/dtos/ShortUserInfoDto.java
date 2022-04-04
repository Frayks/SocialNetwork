package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ShortUserInfoDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String avatarUri;
    private boolean online;

}
