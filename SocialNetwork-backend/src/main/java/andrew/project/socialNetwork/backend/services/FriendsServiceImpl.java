package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.entities.Friends;
import andrew.project.socialNetwork.backend.api.repositories.FriendsRepository;
import andrew.project.socialNetwork.backend.api.services.FriendsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Transactional
public class FriendsServiceImpl implements FriendsService {

    private static final Logger LOGGER = LogManager.getLogger(FriendsServiceImpl.class);

    private FriendsRepository friendsRepository;

    @Override
    public List<Friends> findFriends(Long userId, int limit) {
        return friendsRepository.findFriends(userId, PageRequest.of(0, limit));
    }

    @Override
    public List<Friends> findFriends(Long userId) {
        return friendsRepository.findFriends(userId);
    }

    @Override
    public List<Friends> findRequestsToFriends(Long userId) {
        return friendsRepository.findRequestsToFriends(userId);
    }

    @Override
    public int findNumOfRequestsToFriends(Long userId) {
        return friendsRepository.findNumOfRequestsToFriends(userId);
    }

    @Override
    public Friends checkIfFriends(Long firstUserId, Long secondUserId) throws Exception {
        List<Friends> friendsList = friendsRepository.checkIfFriends(firstUserId, secondUserId);
        if (!CollectionUtils.isEmpty(friendsList)) {
            if (friendsList.size() == 1) {
                return friendsList.get(0);
            }
            throw new Exception("Multiple records found. firstUserId=" + firstUserId + " | secondUserId=" + secondUserId);
        }
        return null;
    }

    @Override
    public Friends save(Friends friends) {
        return friendsRepository.save(friends);
    }

    @Override
    public void delete(Friends friends) {
        friendsRepository.delete(friends);
    }

    @Autowired
    public void setFriendsRepository(FriendsRepository friendsRepository) {
        this.friendsRepository = friendsRepository;
    }
}
