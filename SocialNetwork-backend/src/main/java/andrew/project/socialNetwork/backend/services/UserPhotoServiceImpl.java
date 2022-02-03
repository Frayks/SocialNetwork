package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.entities.UserPhoto;
import andrew.project.socialNetwork.backend.api.repositories.UserPhotoRepository;
import andrew.project.socialNetwork.backend.api.services.UserPhotoService;
import andrew.project.socialNetwork.backend.libraries.MainLibImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<UserPhoto> findByUserId(Long userId) {
        return userPhotoRepository.findByUserId(userId);
    }

    @Override
    public UserPhoto save(UserPhoto userPhoto) {
        return userPhotoRepository.save(userPhoto);
    }

    @Override
    public void delete(UserPhoto userPhoto) {
        userPhotoRepository.delete(userPhoto);
    }

    @Override
    public void deleteByIdAndUserId(Long id, Long userId) {
        userPhotoRepository.deleteByIdAndUserId(id, userId);
    }

    @Autowired
    public void setUserPhotoRepository(UserPhotoRepository userPhotoRepository) {
        this.userPhotoRepository = userPhotoRepository;
    }

}
