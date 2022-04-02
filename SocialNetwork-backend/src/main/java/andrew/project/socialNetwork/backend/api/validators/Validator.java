package andrew.project.socialNetwork.backend.api.validators;

import andrew.project.socialNetwork.backend.api.constants.FormField;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface Validator {
    Map<FormField, String> validateFirstName(String firstName);

    Map<FormField, String> validateLastName(String lastName);

    Map<FormField, String> validateEmail(String email);

    Map<FormField, String> validateUsername(String username, String previousUsername, String email);

    Map<FormField, String> validatePassword(String password);

    Map<FormField, String> validateDate(int dayOfBirth, int monthOfBirth, int yearOfBirth);

    Map<FormField, String> validateSex(String sex);

    Map<FormField, String> validateRestoreKey(String restoreKey);

    Map<FormField, String> validateAboutYourself(String aboutYourself);

    Map<FormField, String> validateCity(String restoreKey);

    Map<FormField, String> validateSchool(String restoreKey);

    Map<FormField, String> validateUniversity(String restoreKey);

    Map<FormField, String> validatePostText(String postText);

    Map<FormField, String> validateImage(MultipartFile image);
}
