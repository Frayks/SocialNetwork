package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.entities.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserChatRepository extends JpaRepository<UserChat, Long> {

    @Query(value = "FROM UserChat uc WHERE uc.firstUserId = ?1 or uc.secondUserId = ?1")
    List<UserChat> findByFirstUserIdOrSecondUserId(Long userId);

    @Query(value = "FROM UserChat uc WHERE (uc.firstUserId = ?1 and uc.secondUserId = ?2) or (uc.firstUserId = ?2 and uc.secondUserId = ?1)")
    List<UserChat> findByChatMembers(Long userId1, Long userId2);

}
