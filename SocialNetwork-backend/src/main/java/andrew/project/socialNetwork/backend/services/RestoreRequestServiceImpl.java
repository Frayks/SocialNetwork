package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.entities.RestoreRequest;
import andrew.project.socialNetwork.backend.api.repositories.RestoreRequestRepository;
import andrew.project.socialNetwork.backend.api.services.RestoreRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class RestoreRequestServiceImpl implements RestoreRequestService {

    private RestoreRequestRepository restoreRequestRepository;

    @Override
    public List<RestoreRequest> findAll() {
        return restoreRequestRepository.findAll();
    }

    @Override
    public RestoreRequest findById(Long id) {
        return restoreRequestRepository.findById(id).orElse(null);
    }

    @Override
    public RestoreRequest findByUserId(Long userId) {
        return restoreRequestRepository.findByUserId(userId);
    }

    @Override
    public RestoreRequest findByRestoreKey(String confirmKey) {
        return restoreRequestRepository.findByRestoreKey(confirmKey);
    }

    @Override
    public RestoreRequest save(RestoreRequest restoreRequest) {
        restoreRequest.setCreationTime(new Timestamp(System.currentTimeMillis()));
        return restoreRequestRepository.save(restoreRequest);
    }

    @Override
    public void delete(RestoreRequest restoreRequest) {
        restoreRequestRepository.delete(restoreRequest);
    }

    @Override
    public void deleteByUserId(Long userId) {
        restoreRequestRepository.deleteByUserId(userId);
    }

    @Autowired
    public void setRestoreRequestRepository(RestoreRequestRepository restoreRequestRepository) {
        this.restoreRequestRepository = restoreRequestRepository;
    }

}