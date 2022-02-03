package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.entities.UserPhoto;

import java.util.List;

public interface UserPhotoService {
    List<UserPhoto> findAll();

    UserPhoto findById(Long id);

    List<UserPhoto> findByUserId(Long userId);

    UserPhoto save(UserPhoto userPhoto);

    void delete(UserPhoto userPhoto);

    void deleteByIdAndUserId(Long id, Long userId);
}
