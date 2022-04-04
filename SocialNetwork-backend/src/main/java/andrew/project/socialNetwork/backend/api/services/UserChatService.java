package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.entities.UserChat;

import java.util.List;

public interface UserChatService {

    List<UserChat> findAll();

    UserChat findById(Long id);

    List<UserChat> findByFirstUserIdOrSecondUserId(Long userId);

    List<UserChat> findByChatMembers(Long userId1, Long userId2);

    UserChat save(UserChat userChat);

    void delete(UserChat userChat);

}
