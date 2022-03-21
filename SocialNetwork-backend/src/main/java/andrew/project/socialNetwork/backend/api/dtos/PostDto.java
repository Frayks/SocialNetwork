package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String photoUri;
    private String text;
    private Integer numOfLikes;
    private Boolean like;
    private ShortUserInfoDto shortUserInfo;
    private String creationTime;
}
