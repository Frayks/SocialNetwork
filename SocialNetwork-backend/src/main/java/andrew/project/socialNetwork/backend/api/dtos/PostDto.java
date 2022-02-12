package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class PostDto {
    private Long id;
    private String photoUrl;
    private String text;
    private Integer numOfLikes;
    private Date creationTime;
    private ShortUserInfoDto shortUserInfo;

}
