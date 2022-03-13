package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.entities.UserPost;

import java.util.List;

public interface UserPostService {
    List<UserPost> findAll();

    UserPost findById(Long id);

    List<UserPost> findByUserIdOrderByCreationTimeDesc(Long userId);

    UserPost save(UserPost userPost);

    void delete(UserPost userPhoto);

    int deleteByIdAndUserId(Long id, Long userId);

    List<UserPost> findByIdAndUserId(Long id, Long userId);

    List<UserPost> findByUserIdsOrderByCreationTimeDesc(List<Long> userIdList);
}
