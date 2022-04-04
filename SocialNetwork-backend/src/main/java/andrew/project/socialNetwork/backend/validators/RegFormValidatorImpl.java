package andrew.project.socialNetwork.backend.validators;

import andrew.project.socialNetwork.backend.api.constants.FormField;
import andrew.project.socialNetwork.backend.api.constants.StatusCode;
import andrew.project.socialNetwork.backend.api.dtos.FormStatusDto;
import andrew.project.socialNetwork.backend.api.dtos.RegFormDto;
import andrew.project.socialNetwork.backend.api.validators.RegFormValidator;
import andrew.project.socialNetwork.backend.api.validators.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RegFormValidatorImpl implements RegFormValidator {

    private Validator validator;

    @Override
    public FormStatusDto validate(RegFormDto regFormDto) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();

        invalidFieldsMap.putAll(validator.validateFirstName(regFormDto.getFirstName()));
        invalidFieldsMap.putAll(validator.validateLastName(regFormDto.getLastName()));
        invalidFieldsMap.putAll(validator.validateEmail(regFormDto.getEmail()));
        invalidFieldsMap.putAll(validator.validateUsername(regFormDto.getUsername(), null, regFormDto.getEmail()));
        invalidFieldsMap.putAll(validator.validatePassword(regFormDto.getPassword()));
        invalidFieldsMap.putAll(validator.validateDate(regFormDto.getDayOfBirth(), regFormDto.getMonthOfBirth(), regFormDto.getYearOfBirth()));
        invalidFieldsMap.putAll(validator.validateSex(regFormDto.getSex()));

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
