package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPostDto {
    private String photoUrl;
    private String text;
    private Integer numOfLikes;

}
