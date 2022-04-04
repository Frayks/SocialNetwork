package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.entities.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, Long> {

    RegistrationRequest findByUsername(String username);

    RegistrationRequest findByEmail(String email);

    RegistrationRequest findByConfirmKey(String confirmKey);

    void deleteByEmail(String email);

}
