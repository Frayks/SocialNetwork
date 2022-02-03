package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class UserPhotoDto {
    private Long id;
    private String photoUrl;
    private Integer numOfLikes;
    private Date loadTime;

}
