package andrew.project.socialNetwork.backend.api.validators;

import andrew.project.socialNetwork.backend.api.constants.FormFields;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface Validator {
    Map<FormFields, String> validateFirstName(String firstName);

    Map<FormFields, String> validateLastName(String lastName);

    Map<FormFields, String> validateEmail(String email);

    Map<FormFields, String> validateUsername(String username, String previousUsername, String email);

    Map<FormFields, String> validatePassword(String password);

    Map<FormFields, String> validateDate(int dayOfBirth, int monthOfBirth, int yearOfBirth);

    Map<FormFields, String> validateSex(String sex);

    Map<FormFields, String> validateRestoreKey(String restoreKey);

    Map<FormFields, String> validateAboutYourself(String aboutYourself);

    Map<FormFields, String> validateCity(String restoreKey);

    Map<FormFields, String> validateSchool(String restoreKey);

    Map<FormFields, String> validateUniversity(String restoreKey);

    Map<FormFields, String> validatePostText(String postText);

    Map<FormFields, String> validateImage(MultipartFile image);
}
