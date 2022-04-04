package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.entities.RegistrationRequest;

import java.util.List;

public interface RegistrationRequestService {

    List<RegistrationRequest> findAll();

    RegistrationRequest findById(Long id);

    RegistrationRequest findByUsername(String username);

    RegistrationRequest findByEmail(String email);

    RegistrationRequest findByConfirmKey(String confirmKey);

    RegistrationRequest save(RegistrationRequest registrationRequest);

    void delete(RegistrationRequest registrationRequest);

    void deleteByEmail(String email);

}
