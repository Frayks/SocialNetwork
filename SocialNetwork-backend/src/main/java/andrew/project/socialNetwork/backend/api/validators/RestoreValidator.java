package andrew.project.socialNetwork.backend.api.validators;

import andrew.project.socialNetwork.backend.api.dtos.FormStatusDto;

public interface RestoreValidator {
    FormStatusDto validate(String email);

}
