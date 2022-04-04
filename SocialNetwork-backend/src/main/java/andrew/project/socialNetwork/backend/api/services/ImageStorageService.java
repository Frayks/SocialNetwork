package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.dtos.SaveImageResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    SaveImageResponseDto saveImage(MultipartFile file) throws Exception;

    void deleteImage(String imageName) throws Exception;

}
