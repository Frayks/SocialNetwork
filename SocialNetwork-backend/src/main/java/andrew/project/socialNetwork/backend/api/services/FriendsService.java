package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.entities.Friends;

import java.util.List;

public interface FriendsService {

    List<Friends> findFriends(Long userId, int limit);

    List<Friends> findRequestsToFriends(Long userId);

    Friends checkIfFriends(Long firstUserId, Long secondUserId) throws Exception;

    Friends save(Friends friends);

    void delete(Friends friends);
}