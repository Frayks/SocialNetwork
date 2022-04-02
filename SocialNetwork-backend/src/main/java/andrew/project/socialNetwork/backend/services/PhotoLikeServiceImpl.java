package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.entities.PhotoLike;
import andrew.project.socialNetwork.backend.api.repositories.PhotoLikeRepository;
import andrew.project.socialNetwork.backend.api.services.PhotoLikeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PhotoLikeServiceImpl implements PhotoLikeService {

    private static final Logger LOGGER = LogManager.getLogger(PhotoLikeServiceImpl.class);

    private PhotoLikeRepository photoLikeRepository;

    @Override
    public PhotoLike findById(Long id) {
        return photoLikeRepository.findById(id).orElse(null);
    }

    @Override
    public List<PhotoLike> findAll() {
        return photoLikeRepository.findAll();
    }

    @Override
    public PhotoLike findByPhotoIdAndUserId(Long photoId, Long userId) {
        return photoLikeRepository.findByPhotoIdAndUserId(photoId, userId);
    }

    @Override
    public List<PhotoLike> findByPhotoIdInAndUserId(List<Long> photoIdList, Long userId) {
        return photoLikeRepository.findByPhotoIdInAndUserId(photoIdList, userId);
    }

    @Override
    public PhotoLike save(PhotoLike photoLike) {
        return photoLikeRepository.save(photoLike);
    }

    @Override
    public void delete(PhotoLike photoLike) {
        photoLikeRepository.delete(photoLike);
    }

    @Override
    public int deleteByPhotoIdAndUserId(Long photoId, Long userId) {
        return photoLikeRepository.deleteByPhotoIdAndUserId(photoId, userId);
    }

    @Autowired
    public void setPhotoLikeRepository(PhotoLikeRepository photoLikeRepository) {
        this.photoLikeRepository = photoLikeRepository;
    }
}
