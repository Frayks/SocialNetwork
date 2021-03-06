package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.entities.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {

    List<UserPhoto> findByUserId(Long userId);

    List<UserPhoto> findByUserIdOrderByLoadTimeDesc(Long userId);

    int deleteByIdAndUserId(Long id, Long userId);

    int deleteByUserId(Long userId);

}
