package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.entities.UserWsSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserWsSessionRepository extends JpaRepository<UserWsSession, Long> {

    List<UserWsSession> findByUserId(Long userId);

    UserWsSession findBySessionId(String sessionId);

    int deleteByUserId(Long userId);

    int deleteBySessionId(String sessionId);

}
