package andrew.project.socialNetwork.backend.validators;

import andrew.project.socialNetwork.backend.api.constants.FormFields;
import andrew.project.socialNetwork.backend.api.constants.Sex;
import andrew.project.socialNetwork.backend.api.entities.RegistrationRequest;
import andrew.project.socialNetwork.backend.api.entities.RestoreRequest;
import andrew.project.socialNetwork.backend.api.properties.FieldsValidationProperties;
import andrew.project.socialNetwork.backend.api.properties.ResourcesProperties;
import andrew.project.socialNetwork.backend.api.services.RegistrationRequestService;
import andrew.project.socialNetwork.backend.api.services.RestoreRequestService;
import andrew.project.socialNetwork.backend.api.services.UserService;
import andrew.project.socialNetwork.backend.api.validators.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Component
public class ValidatorImpl implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(ValidatorImpl.class);

    private static final long BYTES_IN_MB = 1000000;

    private UserService userService;
    private RegistrationRequestService registrationRequestService;
    private RestoreRequestService restoreRequestService;

    private FieldsValidationProperties fieldsValidationProperties;
    private ResourcesProperties resourcesProperties;

    @Override
    public Map<FormFields, String> validateFirstName(String firstName) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();
        if (!firstName.matches(fieldsValidationProperties.getFirstNameRegEx())) {
            invalidFieldsMap.put(FormFields.FIRST_NAME, resourcesProperties.getForbiddenFirstNameErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormFields, String> validateLastName(String lastName) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();
        if (!lastName.matches(fieldsValidationProperties.getLastNameRegEx())) {
            invalidFieldsMap.put(FormFields.LAST_NAME, resourcesProperties.getForbiddenLastNameErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormFields, String> validateEmail(String email) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();
        if (!email.contains("@")) {
            invalidFieldsMap.put(FormFields.EMAIL, resourcesProperties.getInvalidEmailErrorMsg());
        } else if (userService.findByEmail(email) != null) {
            invalidFieldsMap.put(FormFields.EMAIL, resourcesProperties.getBusyEmailErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormFields, String> validateUsername(String username, String previousUsername, String email) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();
        if (!username.equals(previousUsername)) {
            if (!username.matches(fieldsValidationProperties.getUsernameRegEx())) {
                invalidFieldsMap.put(FormFields.USERNAME, resourcesProperties.getForbiddenUsernameErrorMsg());
            } else if (getSplitValues(fieldsValidationProperties.getReservedUsernames()).contains(username)) {
                invalidFieldsMap.put(FormFields.USERNAME, resourcesProperties.getReservedUsernameErrorMsg());
            } else if (userService.findByUsername(username) != null) {
                invalidFieldsMap.put(FormFields.USERNAME, resourcesProperties.getBusyUsernameErrorMsg());
            } else {
                RegistrationRequest registrationRequest = registrationRequestService.findByUsername(username);
                if (registrationRequest != null && !registrationRequest.getEmail().equals(email)) {
                    invalidFieldsMap.put(FormFields.USERNAME, resourcesProperties.getBusyUsernameErrorMsg());
                }
            }
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormFields, String> validatePassword(String password) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();
        if (!password.matches(fieldsValidationProperties.getPasswordRegEx())) {
            invalidFieldsMap.put(FormFields.PASSWORD, resourcesProperties.getForbiddenPasswordErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormFields, String> validateDate(int dayOfBirth, int monthOfBirth, int yearOfBirth) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(yearOfBirth, monthOfBirth - 1, dayOfBirth);
        Calendar calendarNow = Calendar.getInstance();
        Date date = new Date();
        if (calendar.getTimeInMillis() > calendarNow.getTimeInMillis() || calendarNow.get(Calendar.YEAR) - calendar.get(Calendar.YEAR) > fieldsValidationProperties.getMaxDateDiff()) {
            invalidFieldsMap.put(FormFields.DATE_OF_BIRTH, resourcesProperties.getWrongDateOfBirthErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormFields, String> validateSex(String sex) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();
        if (!(sex.equals(Sex.MALE.name()) || sex.equals(Sex.FEMALE.name()))) {
            invalidFieldsMap.put(FormFields.SEX, resourcesProperties.getSexUnknownErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormFields, String> validateRestoreKey(String restoreKey) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();
        RestoreRequest restoreRequest = restoreRequestService.findByRestoreKey(restoreKey);
        if (restoreRequest == null) {
            invalidFieldsMap.put(FormFields.RESTORE_KEY, resourcesProperties.getInvalidRestoreKeyErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormFields, String> validateAboutYourself(String aboutYourself) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();
        if (aboutYourself.length() > fieldsValidationProperties.getMaxAboutYourselfTextSize()) {
            String tooLongTextErrorMsg = String.format(resourcesProperties.getTooLongTextErrorMsg(), fieldsValidationProperties.getMaxAboutYourselfTextSize());
            invalidFieldsMap.put(FormFields.ABOUT_YOURSELF, tooLongTextErrorMsg);
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormFields, String> validateCity(String city) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();
        if (!city.matches(fieldsValidationProperties.getCityRegEx())) {
            invalidFieldsMap.put(FormFields.CITY, resourcesProperties.getForbiddenCityNameErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormFields, String> validateSchool(String school) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();
        if (!school.matches(fieldsValidationProperties.getSchoolRegEx())) {
            invalidFieldsMap.put(FormFields.SCHOOL, resourcesProperties.getForbiddenSchoolNameErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormFields, String> validateUniversity(String university) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();
        if (!university.matches(fieldsValidationProperties.getUniversityRegEx())) {
            invalidFieldsMap.put(FormFields.UNIVERSITY, resourcesProperties.getForbiddenUniversityNameErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormFields, String> validatePostText(String postText) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();
        if (postText.length() > fieldsValidationProperties.getMaxPostTextSize()) {
            String tooLongTextErrorMsg = String.format(resourcesProperties.getTooLongTextErrorMsg(), fieldsValidationProperties.getMaxPostTextSize());
            invalidFieldsMap.put(FormFields.POST_TEXT, tooLongTextErrorMsg);
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormFields, String> validateImage(MultipartFile image) {
        Map<FormFields, String> invalidFieldsMap = new HashMap<>();
        if (image != null) {
            if (image.getSize() > fieldsValidationProperties.getMaxImageSize() * BYTES_IN_MB) {
                String tooLargeImageSizeErrorMsg = String.format(resourcesProperties.getTooLargeImageSizeErrorMsg(), fieldsValidationProperties.getMaxImageSize());
                invalidFieldsMap.put(FormFields.IMAGE, tooLargeImageSizeErrorMsg);
            } else if (!getSplitValues(fieldsValidationProperties.getAllowedImageTypes()).contains(image.getContentType())) {
                String wrongImageTypeErrorMsg = String.format(resourcesProperties.getWrongImageTypeErrorMsg(), fieldsValidationProperties.getAllowedImageTypes());
                invalidFieldsMap.put(FormFields.IMAGE, wrongImageTypeErrorMsg);
            }
        }
        return invalidFieldsMap;
    }

    private List<String> getSplitValues(String property) {
        return Arrays.asList(property.split(fieldsValidationProperties.getSeparator()));
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRegistrationRequestService(RegistrationRequestService registrationRequestService) {
        this.registrationRequestService = registrationRequestService;
    }

    @Autowired
    public void setRestoreRequestService(RestoreRequestService restoreRequestService) {
        this.restoreRequestService = restoreRequestService;
    }

    @Autowired
    public void setFieldsValidationProperties(FieldsValidationProperties fieldsValidationProperties) {
        this.fieldsValidationProperties = fieldsValidationProperties;
    }

    @Autowired
    public void setResourcesProperties(ResourcesProperties resourcesProperties) {
        this.resourcesProperties = resourcesProperties;
    }
}
