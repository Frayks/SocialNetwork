package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.entities.UserPost;

import java.util.List;

public interface UserPostService {
    List<UserPost> findAll();

    UserPost findById(Long id);

    List<UserPost> findByUserId(Long userId);

    UserPost save(UserPost userPost);

    void delete(UserPost userPhoto);

    void deleteByIdAndUserId(Long id, Long userId);
}