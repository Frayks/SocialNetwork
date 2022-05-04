package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.entities.Friends;

import java.util.List;

public interface FriendsService {

    List<Friends> findFriends(Long userId, int limit);

    List<Friends> findFriends(Long userId);

    List<Friends> findRequestsToFriends(Long userId);

    int findNumOfRequestsToFriends(Long userId);

    Friends checkIfFriends(Long firstUserId, Long secondUserId) throws Exception;

    Friends save(Friends friends);

    void delete(Friends friends);

    int deleteByUserId(Long userId);
}
