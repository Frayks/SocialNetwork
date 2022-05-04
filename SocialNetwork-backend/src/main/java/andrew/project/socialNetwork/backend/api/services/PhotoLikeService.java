package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.entities.PhotoLike;

import java.util.List;

public interface PhotoLikeService {

    PhotoLike findById(Long id);

    List<PhotoLike> findAll();

    PhotoLike findByPhotoIdAndUserId(Long photoId, Long userId);

    List<PhotoLike> findByPhotoIdInAndUserId(List<Long> photoIdList, Long userId);

    PhotoLike save(PhotoLike photoLike);

    void delete(PhotoLike photoLike);

    int deleteByPhotoIdAndUserId(Long photoId, Long userId);

    int deleteByPhotoIdIn(List<Long> photoIdList);
}
