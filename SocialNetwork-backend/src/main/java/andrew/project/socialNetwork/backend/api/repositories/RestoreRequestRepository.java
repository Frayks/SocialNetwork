package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.entities.RestoreRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RestoreRequestRepository extends JpaRepository<RestoreRequest, Long> {

    RestoreRequest findByUserId(Long userId);

    RestoreRequest findByRestoreKey(String restoreKey);

    void deleteByUserId(Long userId);
}
