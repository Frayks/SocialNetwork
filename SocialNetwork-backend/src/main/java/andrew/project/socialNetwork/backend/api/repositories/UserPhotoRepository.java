package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.entities.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {

    List<UserPhoto> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM UserPhoto up WHERE up.id = ?1 AND up.userId = ?2")
    int deleteByIdAndUserId(Long id, Long userId);

}
