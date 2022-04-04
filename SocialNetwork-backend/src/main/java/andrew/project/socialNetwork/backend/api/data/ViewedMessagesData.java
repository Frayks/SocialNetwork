package andrew.project.socialNetwork.backend.api.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ViewedMessagesData {
    private Long chatId;
    private List<Long> viewedMessagesIdsList;

}
