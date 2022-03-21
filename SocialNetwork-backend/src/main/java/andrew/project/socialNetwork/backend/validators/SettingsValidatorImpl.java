package andrew.project.socialNetwork.backend.validators;

import andrew.project.socialNetwork.backend.api.constants.FormFields;
import andrew.project.socialNetwork.backend.api.constants.StatusCode;
import andrew.project.socialNetwork.backend.api.dtos.AdditionalSettingsDto;
import andrew.project.socialNetwork.backend.api.dtos.BasicSettingsDto;
import andrew.project.socialNetwork.backend.api.dtos.FormStatusDto;
import andrew.project.socialNetwork.backend.api.validators.SettingsValidator;
import andrew.project.socialNetwork.backend.api.validators.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Component
public class SettingsValidatorImpl implements SettingsValidator {

    private Validator validator;

    @Override
    public FormStatusDto validateBasicSettings(MultipartFile image, BasicSettingsDto basicSettingsDto, String previousUsername, String email) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();

        invalidFieldsMap.putAll(validator.validateImage(image));
        invalidFieldsMap.putAll(validator.validateFirstName(basicSettingsDto.getFirstName()));
        invalidFieldsMap.putAll(validator.validateLastName(basicSettingsDto.getLastName()));
        invalidFieldsMap.putAll(validator.validateUsername(basicSettingsDto.getUsername(), previousUsername, email));
        invalidFieldsMap.putAll(validator.validateDate(basicSettingsDto.getDayOfBirth(), basicSettingsDto.getMonthOfBirth(), basicSettingsDto.getYearOfBirth()));
        invalidFieldsMap.putAll(validator.validateSex(basicSettingsDto.getSex()));

        FormStatusDto formStatusDto = new FormStatusDto();
        if (invalidFieldsMap.size() > 0) {
            formStatusDto.setStatus(StatusCode.FAILURE);
            formStatusDto.setInvalidFieldsMap(invalidFieldsMap);
        } else {
            formStatusDto.setStatus(StatusCode.SUCCESS);
        }
        return formStatusDto;
    }

    @Override
    public FormStatusDto validateAdditionalSettings(AdditionalSettingsDto additionalSettingsDto) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();

        invalidFieldsMap.putAll(validator.validateAboutYourself(additionalSettingsDto.getAboutYourself()));
        invalidFieldsMap.putAll(validator.validateCity(additionalSettingsDto.getCity()));
        invalidFieldsMap.putAll(validator.validateSchool(additionalSettingsDto.getSchool()));
        invalidFieldsMap.putAll(validator.validateUniversity(additionalSettingsDto.getUniversity()));

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
