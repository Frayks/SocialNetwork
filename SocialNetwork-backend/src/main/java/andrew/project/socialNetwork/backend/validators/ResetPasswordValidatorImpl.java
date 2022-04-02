package andrew.project.socialNetwork.backend.validators;

import andrew.project.socialNetwork.backend.api.constants.FormField;
import andrew.project.socialNetwork.backend.api.constants.StatusCode;
import andrew.project.socialNetwork.backend.api.dtos.FormStatusDto;
import andrew.project.socialNetwork.backend.api.dtos.ResetPasswordRequestDto;
import andrew.project.socialNetwork.backend.api.validators.ResetPasswordValidator;
import andrew.project.socialNetwork.backend.api.validators.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ResetPasswordValidatorImpl implements ResetPasswordValidator {

    private Validator validator;

    @Override
    public FormStatusDto validate(ResetPasswordRequestDto resetPasswordRequestDto) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();

        invalidFieldsMap.putAll(validator.validateRestoreKey(resetPasswordRequestDto.getRestoreKey()));
        invalidFieldsMap.putAll(validator.validatePassword(resetPasswordRequestDto.getNewPassword()));

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
