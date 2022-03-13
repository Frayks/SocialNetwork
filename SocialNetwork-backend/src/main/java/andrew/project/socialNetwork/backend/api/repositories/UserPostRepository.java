package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.entities.UserPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserPostRepository extends JpaRepository<UserPost, Long> {

    List<UserPost> findByUserIdOrderByCreationTimeDesc(Long userId);

    @Modifying
    @Query("DELETE FROM UserPost up WHERE up.id = ?1 AND up.userId = ?2")
    int deleteByIdAndUserId(Long id, Long userId);


    List<UserPost> findByIdAndUserId(Long id, Long userId);

    @Query( "FROM UserPost up WHERE up.userId in ?1 order by up.creationTime desc " )
    List<UserPost> findByUserIdsOrderByCreationTimeDesc(List<Long> userIdList);


}
