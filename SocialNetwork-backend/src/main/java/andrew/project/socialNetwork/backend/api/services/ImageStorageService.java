package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.dtos.SaveImageResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStorageService {

    SaveImageResponseDto saveImage(MultipartFile file) throws Exception;

    void deleteImage(String imageName);

    void deleteImageList(List<String> imageNameList);
}
