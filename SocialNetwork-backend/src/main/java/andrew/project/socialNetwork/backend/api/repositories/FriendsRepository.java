package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.entities.Friends;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface FriendsRepository extends JpaRepository<Friends, Long> {

    @Query(value = "FROM Friends f WHERE (f.firstUserId = ?1 or f.secondUserId = ?1) and f.accepted = true")
    List<Friends> findFriends(Long userId, PageRequest pageRequest);

    @Query(value = "FROM Friends f WHERE (f.firstUserId = ?1 or f.secondUserId = ?1) and f.accepted = true")
    List<Friends> findFriends(Long userId);

    @Query(value = "FROM Friends f WHERE f.secondUserId = ?1 and f.accepted = false")
    List<Friends> findRequestsToFriends(Long userId);

    @Query(value = "FROM Friends f WHERE (f.firstUserId = ?1 AND f.secondUserId = ?2) OR (f.firstUserId = ?2 AND f.secondUserId = ?1)")
    List<Friends> checkIfFriends(Long firstUserId, Long secondUserId);

}
