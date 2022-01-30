package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.entities.UserPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserPostRepository extends JpaRepository<UserPost, Long> {

    List<UserPost> findByUserId(Long userId);

}
