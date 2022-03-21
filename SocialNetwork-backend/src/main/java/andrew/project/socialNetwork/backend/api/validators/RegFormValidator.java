package andrew.project.socialNetwork.backend.api.validators;

import andrew.project.socialNetwork.backend.api.dtos.FormStatusDto;
import andrew.project.socialNetwork.backend.api.dtos.RegFormDto;

public interface RegFormValidator {
    FormStatusDto validate(RegFormDto regFormDto);
}
