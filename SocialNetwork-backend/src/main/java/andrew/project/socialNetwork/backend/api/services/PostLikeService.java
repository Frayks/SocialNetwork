package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.entities.PostLike;

import java.util.List;

public interface PostLikeService {
    PostLike findById(Long id);

    List<PostLike> findAll();

    PostLike findByPostIdAndUserId(Long postId, Long userId);

    List<PostLike> findByPostIdInAndUserId(List<Long> postIdList, Long userId);

    PostLike save(PostLike postLike);

    void delete(PostLike postLike);

    int deleteByPostIdAndUserId(Long postId, Long userId);
}
