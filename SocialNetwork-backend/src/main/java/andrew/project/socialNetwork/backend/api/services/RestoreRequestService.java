package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.entities.RestoreRequest;

import java.util.List;

public interface RestoreRequestService {

    List<RestoreRequest> findAll();

    RestoreRequest findById(Long id);

    RestoreRequest findByUserId(Long userId);

    RestoreRequest findByRestoreKey(String confirmKey);

    RestoreRequest save(RestoreRequest restoreRequest);

    void delete(RestoreRequest restoreRequest);

    void deleteByUserId(Long id);
}
