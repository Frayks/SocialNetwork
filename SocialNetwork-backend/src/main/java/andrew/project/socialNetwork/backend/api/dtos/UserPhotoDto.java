package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserPhotoDto {
    private String photoUrl;
    private Integer numOfLikes;

}
