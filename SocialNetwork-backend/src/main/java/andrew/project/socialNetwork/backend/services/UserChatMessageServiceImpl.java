package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.entities.UserChatMessage;
import andrew.project.socialNetwork.backend.api.repositories.UserChatMessageRepository;
import andrew.project.socialNetwork.backend.api.services.UserChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class UserChatMessageServiceImpl implements UserChatMessageService {

    private UserChatMessageRepository userChatMessageRepository;

    @Override
    public List<UserChatMessage> findAll() {
        return userChatMessageRepository.findAll();
    }

    @Override
    public UserChatMessage findById(Long id) {
        return userChatMessageRepository.findById(id).orElse(null);
    }

    @Override
    public List<UserChatMessage> findByChatIdAndCreationTimeBeforeOrderByCreationTimeDesc(Long chatId, Timestamp beforeTime, int limit) {
        if (beforeTime != null) {
            return userChatMessageRepository.findByChatIdAndCreationTimeBeforeOrderByCreationTimeDesc(chatId, beforeTime, PageRequest.of(0, limit));
        } else {
            return userChatMessageRepository.findByChatIdOrderByCreationTimeDesc(chatId, PageRequest.of(0, limit));
        }
    }

    @Override
    public List<UserChatMessage> findByIdInAndChatIdAndUserIdAndRevised(List<Long> idList, Long chatId, Long userId, Boolean revised) {
        return userChatMessageRepository.findByIdInAndChatIdAndUserIdAndRevised(idList, chatId, userId, revised);
    }

    @Override
    public UserChatMessage setCreationTimeAndSave(UserChatMessage userChatMessage) {
        userChatMessage.setCreationTime(new Timestamp(System.currentTimeMillis()));
        return userChatMessageRepository.save(userChatMessage);
    }

    @Override
    public List<UserChatMessage> saveAll(List<UserChatMessage> userChatMessageList) {
        return userChatMessageRepository.saveAll(userChatMessageList);
    }

    @Override
    public void delete(UserChatMessage userChatMessage) {
        userChatMessageRepository.delete(userChatMessage);
    }

    @Autowired
    public void setUserChatMessageRepository(UserChatMessageRepository userChatMessageRepository) {
        this.userChatMessageRepository = userChatMessageRepository;
    }

}
