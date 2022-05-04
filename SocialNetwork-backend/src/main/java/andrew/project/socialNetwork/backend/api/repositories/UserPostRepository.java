package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.entities.UserPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Transactional
public interface UserPostRepository extends JpaRepository<UserPost, Long> {

    int deleteByIdAndUserId(Long id, Long userId);

    int deleteByUserId(Long userId);

    int countByUserId(Long userId);

    List<UserPost> findByUserId(Long userId);

    List<UserPost> findByUserIdOrderByCreationTimeDesc(Long userId, Pageable pageable);

    List<UserPost> findByUserIdInOrderByCreationTimeDesc(List<Long> userIdList, Pageable pageable);

    List<UserPost> findByUserIdAndCreationTimeBeforeOrderByCreationTimeDesc(Long userId, Timestamp beforeTime, Pageable pageable);

    List<UserPost> findByUserIdInAndCreationTimeBeforeOrderByCreationTimeDesc(List<Long> userIdList, Timestamp beforeTime, Pageable pageable);

}
