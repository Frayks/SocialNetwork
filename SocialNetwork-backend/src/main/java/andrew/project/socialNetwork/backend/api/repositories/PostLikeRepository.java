package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.entities.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    PostLike findByPostIdAndUserId(Long postId, Long userId);

    List<PostLike> findByPostIdInAndUserId(List<Long> postIdList, Long userId);

    int deleteByPostIdAndUserId(Long postId, Long userId);

    int deleteByPostIdIn(List<Long> postIdList);
}
