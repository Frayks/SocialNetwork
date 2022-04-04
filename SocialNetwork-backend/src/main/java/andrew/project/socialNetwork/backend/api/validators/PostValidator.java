package andrew.project.socialNetwork.backend.api.validators;

import andrew.project.socialNetwork.backend.api.dtos.FormStatusDto;
import org.springframework.web.multipart.MultipartFile;

public interface PostValidator {

    FormStatusDto validate(MultipartFile image, String text);

}
