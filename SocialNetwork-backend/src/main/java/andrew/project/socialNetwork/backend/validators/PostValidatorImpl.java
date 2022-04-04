package andrew.project.socialNetwork.backend.validators;

import andrew.project.socialNetwork.backend.api.constants.FormField;
import andrew.project.socialNetwork.backend.api.constants.StatusCode;
import andrew.project.socialNetwork.backend.api.dtos.FormStatusDto;
import andrew.project.socialNetwork.backend.api.properties.ResourcesProperties;
import andrew.project.socialNetwork.backend.api.validators.PostValidator;
import andrew.project.socialNetwork.backend.api.validators.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Component
public class PostValidatorImpl implements PostValidator {

    private Validator validator;
    private ResourcesProperties resourcesProperties;

    @Override
    public FormStatusDto validate(MultipartFile image, String text) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();

        if (image == null && !StringUtils.hasText(text)) {
            invalidFieldsMap.put(FormField.ALL_FIELDS, resourcesProperties.getOneFilledFieldIsRequiredErrorMsg());
        } else {
            invalidFieldsMap.putAll(validator.validateImage(image));
            invalidFieldsMap.putAll(validator.validatePostText(text));
        }

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

    @Autowired
    public void setResourcesProperties(ResourcesProperties resourcesProperties) {
        this.resourcesProperties = resourcesProperties;
    }
}
