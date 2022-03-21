package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.entities.UserPhoto;
import andrew.project.socialNetwork.backend.api.repositories.UserPhotoRepository;
import andrew.project.socialNetwork.backend.api.services.UserPhotoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class UserPhotoServiceImpl implements UserPhotoService {

    private static final Logger LOGGER = LogManager.getLogger(UserPhotoServiceImpl.class);

    private UserPhotoRepository userPhotoRepository;

    @Override
    public List<UserPhoto> findAll() {
        return userPhotoRepository.findAll();
    }

    @Override
    public UserPhoto findById(Long id) {
        return userPhotoRepository.findById(id).orElse(null);
    }

    @Override
    public List<UserPhoto> findByUserIdOrderByLoadTimeDesc(Long userId) {
        return userPhotoRepository.findByUserIdOrderByLoadTimeDesc(userId);
    }

    @Override
    public List<UserPhoto> findByUserIdInOrderByLoadTimeDesc(List<Long> userIdList) {
        return userPhotoRepository.findByUserIdInOrderByLoadTimeDesc(userIdList);
    }

    @Override
    public UserPhoto save(UserPhoto userPhoto) {
        userPhoto.setLoadTime(new Timestamp(System.currentTimeMillis()));
        return userPhotoRepository.save(userPhoto);
    }

    @Override
    public void delete(UserPhoto userPhoto) {
        userPhotoRepository.delete(userPhoto);
    }

    @Override
    public int deleteByIdAndUserId(Long id, Long userId) {
        return userPhotoRepository.deleteByIdAndUserId(id, userId);
    }

    @Autowired
    public void setUserPhotoRepository(UserPhotoRepository userPhotoRepository) {
        this.userPhotoRepository = userPhotoRepository;
    }

}
