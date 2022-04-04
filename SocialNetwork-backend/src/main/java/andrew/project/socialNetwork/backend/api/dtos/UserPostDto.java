package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPostDto {
    private Long id;
    private String photoUri;
    private String text;
    private Integer numOfLikes;
    private Boolean like = false;
    private String creationTime;

}
