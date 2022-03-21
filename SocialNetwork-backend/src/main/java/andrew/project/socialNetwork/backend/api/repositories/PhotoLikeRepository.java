package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.entities.PhotoLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface PhotoLikeRepository extends JpaRepository<PhotoLike, Long> {

    PhotoLike findByPhotoIdAndUserId(Long photoId, Long userId);

    List<PhotoLike> findByPhotoIdInAndUserId(List<Long> photoIdList, Long userId);

    int deleteByPhotoIdAndUserId(Long photoId, Long userId);

}
