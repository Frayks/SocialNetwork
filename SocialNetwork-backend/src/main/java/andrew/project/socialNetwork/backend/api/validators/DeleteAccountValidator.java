package andrew.project.socialNetwork.backend.api.validators;

import andrew.project.socialNetwork.backend.api.dtos.FormStatusDto;

public interface DeleteAccountValidator {

    FormStatusDto validate(String password, String passwordHash);

}
