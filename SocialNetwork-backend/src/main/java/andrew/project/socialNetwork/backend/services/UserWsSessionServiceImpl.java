package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.entities.UserWsSession;
import andrew.project.socialNetwork.backend.api.repositories.UserWsSessionRepository;
import andrew.project.socialNetwork.backend.api.services.UserWsSessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserWsSessionServiceImpl implements UserWsSessionService {

    private static final Logger LOGGER = LogManager.getLogger(UserWsSessionServiceImpl.class);

    private UserWsSessionRepository userWsSessionRepository;

    @Override
    public List<UserWsSession> findAll() {
        return userWsSessionRepository.findAll();
    }

    @Override
    public UserWsSession findById(Long id) {
        return userWsSessionRepository.findById(id).orElse(null);
    }

    @Override
    public List<UserWsSession> findByUserId(Long userId) {
        return userWsSessionRepository.findByUserId(userId);
    }

    @Override
    public UserWsSession findBySessionId(String sessionId) {
        return userWsSessionRepository.findBySessionId(sessionId);
    }

    @Override
    public UserWsSession save(UserWsSession userWsSession) {
        return userWsSessionRepository.save(userWsSession);
    }

    @Override
    public void delete(UserWsSession userWsSession) {
        userWsSessionRepository.delete(userWsSession);
    }

    @Override
    public int deleteByUserId(Long userId) {
        return userWsSessionRepository.deleteByUserId(userId);
    }

    @Override
    public int deleteBySessionId(String sessionId) {
        return userWsSessionRepository.deleteBySessionId(sessionId);
    }

    @Override
    public void deleteAll() {
        userWsSessionRepository.deleteAll();
    }

    @Autowired
    public void setUserWsSessionRepository(UserWsSessionRepository userWsSessionRepository) {
        this.userWsSessionRepository = userWsSessionRepository;
    }

}
