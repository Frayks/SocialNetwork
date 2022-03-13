package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.entities.UserPost;
import andrew.project.socialNetwork.backend.api.repositories.UserPostRepository;
import andrew.project.socialNetwork.backend.api.services.UserPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class UserPostServiceImpl implements UserPostService {

    private UserPostRepository userPostRepository;

    @Override
    public List<UserPost> findAll() {
        return userPostRepository.findAll();
    }

    @Override
    public UserPost findById(Long id) {
        return userPostRepository.findById(id).orElse(null);
    }

    @Override
    public List<UserPost> findByUserIdOrderByCreationTimeDesc(Long userId) {
        return userPostRepository.findByUserIdOrderByCreationTimeDesc(userId);
    }

    @Override
    public UserPost save(UserPost userPost) {
        userPost.setCreationTime(new Timestamp(System.currentTimeMillis()));
        return userPostRepository.save(userPost);
    }

    @Override
    public void delete(UserPost userPhoto) {
        userPostRepository.delete(userPhoto);
    }

    @Override
    public int deleteByIdAndUserId(Long id, Long userId) {
        return userPostRepository.deleteByIdAndUserId(id, userId);
    }

    @Override
    public List<UserPost> findByIdAndUserId(Long id, Long userId) {
        return userPostRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public List<UserPost> findByUserIdsOrderByCreationTimeDesc(List<Long> userIdList) {
        return userPostRepository.findByUserIdsOrderByCreationTimeDesc(userIdList);
    }

    @Autowired
    public void setUserPostRepository(UserPostRepository userPostRepository) {
        this.userPostRepository = userPostRepository;
    }

}
