package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.entities.UserChat;
import andrew.project.socialNetwork.backend.api.repositories.UserChatRepository;
import andrew.project.socialNetwork.backend.api.services.UserChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserChatServiceImpl implements UserChatService {

    private UserChatRepository userChatRepository;

    @Override
    public List<UserChat> findAll() {
        return userChatRepository.findAll();
    }

    @Override
    public UserChat findById(Long id) {
        return userChatRepository.findById(id).orElse(null);
    }

    @Override
    public List<UserChat> findByFirstUserIdOrSecondUserId(Long userId) {
        return userChatRepository.findByFirstUserIdOrSecondUserId(userId);
    }

    @Override
    public List<UserChat> findByChatMembers(Long userId1, Long userId2) {
        return userChatRepository.findByChatMembers(userId1, userId2);
    }

    @Override
    public UserChat save(UserChat userChat) {
        return userChatRepository.save(userChat);
    }

    @Override
    public void delete(UserChat userChat) {
        userChatRepository.delete(userChat);
    }

    @Autowired
    public void setUserChatRepository(UserChatRepository userChatRepository) {
        this.userChatRepository = userChatRepository;
    }
}
