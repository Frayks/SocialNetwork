package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.entities.RegistrationRequest;
import andrew.project.socialNetwork.backend.api.repositories.RegistrationRequestRepository;
import andrew.project.socialNetwork.backend.api.services.RegistrationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class RegistrationRequestServiceImpl implements RegistrationRequestService {

    private RegistrationRequestRepository registrationRequestRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public List<RegistrationRequest> findAll() {
        return registrationRequestRepository.findAll();
    }

    @Override
    public RegistrationRequest findById(Long id) {
        return registrationRequestRepository.findById(id).orElse(null);
    }

    @Override
    public RegistrationRequest findByUsername(String username) {
        return registrationRequestRepository.findByUsername(username);
    }

    @Override
    public RegistrationRequest findByEmail(String email) {
        return registrationRequestRepository.findByEmail(email);
    }

    @Override
    public RegistrationRequest findByConfirmKey(String confirmKey) {
        return registrationRequestRepository.findByConfirmKey(confirmKey);
    }

    @Override
    public RegistrationRequest save(RegistrationRequest registrationRequest) {
        registrationRequest.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        registrationRequest.setCreationTime(new Timestamp(System.currentTimeMillis()));
        return registrationRequestRepository.save(registrationRequest);
    }

    @Override
    public void delete(RegistrationRequest registrationRequest) {
        registrationRequestRepository.delete(registrationRequest);
    }

    @Override
    public void deleteByEmail(String email) {
        registrationRequestRepository.deleteByEmail(email);
    }

    @Autowired
    public void setRegistrationRequestRepository(RegistrationRequestRepository registrationRequestRepository) {
        this.registrationRequestRepository = registrationRequestRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
