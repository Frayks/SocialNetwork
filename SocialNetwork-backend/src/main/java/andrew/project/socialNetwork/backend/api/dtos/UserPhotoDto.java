package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPhotoDto {
    private Long id;
    private String photoUri;
    private Integer numOfLikes;
    private Boolean like = false;
    private String loadTime;

}
