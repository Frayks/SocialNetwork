package andrew.project.socialNetwork.backend.api.validators;

import andrew.project.socialNetwork.backend.api.dtos.AdditionalSettingsDto;
import andrew.project.socialNetwork.backend.api.dtos.BasicSettingsDto;
import andrew.project.socialNetwork.backend.api.dtos.FormStatusDto;
import org.springframework.web.multipart.MultipartFile;

public interface SettingsValidator {

    FormStatusDto validateBasicSettings(MultipartFile image, BasicSettingsDto basicSettingsDto, String previousUsername, String email);

    FormStatusDto validateAdditionalSettings(AdditionalSettingsDto additionalSettingsDto);
    
}
