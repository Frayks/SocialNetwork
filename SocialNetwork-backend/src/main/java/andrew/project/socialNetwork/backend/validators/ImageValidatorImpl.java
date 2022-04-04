package andrew.project.socialNetwork.backend.validators;

import andrew.project.socialNetwork.backend.api.constants.FormField;
import andrew.project.socialNetwork.backend.api.constants.StatusCode;
import andrew.project.socialNetwork.backend.api.dtos.FormStatusDto;
import andrew.project.socialNetwork.backend.api.validators.ImageValidator;
import andrew.project.socialNetwork.backend.api.validators.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Component
public class ImageValidatorImpl implements ImageValidator {

    private Validator validator;

    @Override
    public FormStatusDto validate(MultipartFile image) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();

        invalidFieldsMap.putAll(validator.validateImage(image));

        FormStatusDto formStatusDto = new FormStatusDto();
        if (invalidFieldsMap.size() > 0) {
            formStatusDto.setStatus(StatusCode.FAILURE);
            formStatusDto.setInvalidFieldsMap(invalidFieldsMap);
        } else {
            formStatusDto.setStatus(StatusCode.SUCCESS);
        }
        return formStatusDto;
    }

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

}
