package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.entities.PostLike;
import andrew.project.socialNetwork.backend.api.repositories.PostLikeRepository;
import andrew.project.socialNetwork.backend.api.services.PostLikeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostLikeServiceImpl implements PostLikeService {

    private static final Logger LOGGER = LogManager.getLogger(PostLikeServiceImpl.class);

    private PostLikeRepository postLikeRepository;

    @Override
    public PostLike findById(Long id) {
        return postLikeRepository.findById(id).orElse(null);
    }

    @Override
    public List<PostLike> findAll() {
        return postLikeRepository.findAll();
    }

    @Override
    public PostLike findByPostIdAndUserId(Long postId, Long userId) {
        return postLikeRepository.findByPostIdAndUserId(postId, userId);
    }

    @Override
    public List<PostLike> findByPostIdInAndUserId(List<Long> postIdList, Long userId) {
        return postLikeRepository.findByPostIdInAndUserId(postIdList, userId);
    }

    @Override
    public PostLike save(PostLike postLike) {
        return postLikeRepository.save(postLike);
    }

    @Override
    public void delete(PostLike postLike) {
        postLikeRepository.delete(postLike);
    }

    @Override
    public int deleteByPostIdAndUserId(Long postId, Long userId) {
        return postLikeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Autowired
    public void setPostLikeRepository(PostLikeRepository postLikeRepository) {
        this.postLikeRepository = postLikeRepository;
    }
}
