package andrew.project.socialNetwork.backend.api.validators;

import andrew.project.socialNetwork.backend.api.dtos.FormStatusDto;
import andrew.project.socialNetwork.backend.api.dtos.ResetPasswordRequestDto;

public interface ResetPasswordValidator {

    FormStatusDto validate(ResetPasswordRequestDto resetPasswordRequestDto);

}
