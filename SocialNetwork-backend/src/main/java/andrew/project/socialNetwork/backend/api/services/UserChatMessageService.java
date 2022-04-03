package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.entities.UserChatMessage;

import java.sql.Timestamp;
import java.util.List;

public interface UserChatMessageService {
    List<UserChatMessage> findAll();

    UserChatMessage findById(Long id);

    List<UserChatMessage> findByChatIdAndCreationTimeBeforeOrderByCreationTimeDesc(Long chatId, Timestamp beforeTime, int limit);

    List<UserChatMessage> findByIdInAndChatIdAndUserIdAndRevised(List<Long> idList, Long chatId, Long userId, Boolean revised);

    UserChatMessage setCreationTimeAndSave(UserChatMessage userChatMessage);

    List<UserChatMessage> saveAll(List<UserChatMessage> userChatMessageList);

    void delete(UserChatMessage userChatMessage);
}
