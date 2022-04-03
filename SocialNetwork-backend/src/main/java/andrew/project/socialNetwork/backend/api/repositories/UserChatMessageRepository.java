package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.entities.UserChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Transactional
public interface UserChatMessageRepository extends JpaRepository<UserChatMessage, Long> {

    List<UserChatMessage> findByChatIdOrderByCreationTimeDesc(Long chatId, Pageable pageable);

    List<UserChatMessage> findByChatIdAndCreationTimeBeforeOrderByCreationTimeDesc(Long chatId, Timestamp beforeTime, Pageable pageable);

    List<UserChatMessage> findByIdInAndChatIdAndUserIdAndRevised(List<Long> idList, Long chatId, Long userId, Boolean revised);

}
