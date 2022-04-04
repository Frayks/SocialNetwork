package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.entities.UserPost;

import java.sql.Timestamp;
import java.util.List;

public interface UserPostService {

    List<UserPost> findAll();

    UserPost findById(Long id);

    int countByUserId(Long userId);

    List<UserPost> findByUserIdAndCreationTimeBeforeOrderByCreationTimeDesc(Long userId, Timestamp beforeTime, int limit);

    List<UserPost> findByUserIdInAndCreationTimeBeforeOrderByCreationTimeDesc(List<Long> userIdList, Timestamp beforeTime, int limit);

    UserPost save(UserPost userPost);

    void delete(UserPost userPhoto);

    int deleteByIdAndUserId(Long id, Long userId);

}
