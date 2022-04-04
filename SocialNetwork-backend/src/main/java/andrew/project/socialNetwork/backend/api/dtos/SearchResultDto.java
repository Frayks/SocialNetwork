package andrew.project.socialNetwork.backend.api.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SearchResultDto {
    private List<ShortUserInfoDto> userList = new ArrayList<>();

}
