package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.entities.UserPhoto;

import java.util.List;

public interface UserPhotoService {
    List<UserPhoto> findAll();

    UserPhoto findById(Long id);

    List<UserPhoto> findByUserIdOrderByLoadTimeDesc(Long userId);

    List<UserPhoto> findByUserIdInOrderByLoadTimeDesc(List<Long> userIdList);

    UserPhoto save(UserPhoto userPhoto);

    void delete(UserPhoto userPhoto);

    int deleteByIdAndUserId(Long id, Long userId);


}
