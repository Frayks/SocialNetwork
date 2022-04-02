package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.entities.UserWsSession;

import java.util.List;

public interface UserWsSessionService {
    List<UserWsSession> findAll();

    UserWsSession findById(Long id);

    List<UserWsSession> findByUserId(Long userId);

    UserWsSession findBySessionId(String sessionId);

    UserWsSession save(UserWsSession userWsSession);

    void delete(UserWsSession userWsSession);

    int deleteByUserId(Long userId);

    int deleteBySessionId(String sessionId);

    void deleteAll();
}
