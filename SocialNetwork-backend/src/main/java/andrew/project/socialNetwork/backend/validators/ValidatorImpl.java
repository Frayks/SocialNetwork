package andrew.project.socialNetwork.backend.validators;

import andrew.project.socialNetwork.backend.api.constants.FormField;
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
    public Map<FormField, String> validateFirstName(String firstName) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        if (firstName == null) {
            invalidFieldsMap.put(FormField.FIRST_NAME, resourcesProperties.getNullFieldErrorMsg());
        } else if (!firstName.matches(fieldsValidationProperties.getFirstNameRegEx())) {
            invalidFieldsMap.put(FormField.FIRST_NAME, resourcesProperties.getForbiddenFirstNameErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormField, String> validateLastName(String lastName) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        if (lastName == null) {
            invalidFieldsMap.put(FormField.LAST_NAME, resourcesProperties.getNullFieldErrorMsg());
        } else if (!lastName.matches(fieldsValidationProperties.getLastNameRegEx())) {
            invalidFieldsMap.put(FormField.LAST_NAME, resourcesProperties.getForbiddenLastNameErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormField, String> validateEmail(String email) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        if (email == null) {
            invalidFieldsMap.put(FormField.EMAIL, resourcesProperties.getNullFieldErrorMsg());
        } else if (!email.contains("@")) {
            invalidFieldsMap.put(FormField.EMAIL, resourcesProperties.getInvalidEmailErrorMsg());
        } else if (userService.findByEmail(email) != null) {
            invalidFieldsMap.put(FormField.EMAIL, resourcesProperties.getBusyEmailErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormField, String> validateUsername(String username, String previousUsername, String email) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        if (username == null) {
            invalidFieldsMap.put(FormField.USERNAME, resourcesProperties.getNullFieldErrorMsg());
        } else if (email == null) {
            invalidFieldsMap.put(FormField.EMAIL, resourcesProperties.getNullFieldErrorMsg());
        } else if (!username.equals(previousUsername)) {
            if (!username.matches(fieldsValidationProperties.getUsernameRegEx())) {
                invalidFieldsMap.put(FormField.USERNAME, resourcesProperties.getForbiddenUsernameErrorMsg());
            } else if (getSplitValues(fieldsValidationProperties.getReservedUsernames()).contains(username)) {
                invalidFieldsMap.put(FormField.USERNAME, resourcesProperties.getReservedUsernameErrorMsg());
            } else if (userService.findByUsername(username) != null) {
                invalidFieldsMap.put(FormField.USERNAME, resourcesProperties.getBusyUsernameErrorMsg());
            } else {
                RegistrationRequest registrationRequest = registrationRequestService.findByUsername(username);
                if (registrationRequest != null && !registrationRequest.getEmail().equals(email)) {
                    invalidFieldsMap.put(FormField.USERNAME, resourcesProperties.getBusyUsernameErrorMsg());
                }
            }
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormField, String> validatePassword(String password) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        if (password == null) {
            invalidFieldsMap.put(FormField.PASSWORD, resourcesProperties.getNullFieldErrorMsg());
        } else if (!password.matches(fieldsValidationProperties.getPasswordRegEx())) {
            invalidFieldsMap.put(FormField.PASSWORD, resourcesProperties.getForbiddenPasswordErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormField, String> validateDate(int dayOfBirth, int monthOfBirth, int yearOfBirth) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(yearOfBirth, monthOfBirth - 1, dayOfBirth);
        Calendar calendarNow = Calendar.getInstance();
        Date date = new Date();
        if (calendar.getTimeInMillis() > calendarNow.getTimeInMillis() || calendarNow.get(Calendar.YEAR) - calendar.get(Calendar.YEAR) > fieldsValidationProperties.getMaxDateDiff()) {
            invalidFieldsMap.put(FormField.DATE_OF_BIRTH, resourcesProperties.getWrongDateOfBirthErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormField, String> validateSex(String sex) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        if (sex == null) {
            invalidFieldsMap.put(FormField.SEX, resourcesProperties.getNullFieldErrorMsg());
        } else if (!(sex.equals(Sex.MALE.name()) || sex.equals(Sex.FEMALE.name()))) {
            invalidFieldsMap.put(FormField.SEX, resourcesProperties.getSexUnknownErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormField, String> validateRestoreKey(String restoreKey) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        RestoreRequest restoreRequest = restoreRequestService.findByRestoreKey(restoreKey);
        if (restoreRequest == null) {
            invalidFieldsMap.put(FormField.RESTORE_KEY, resourcesProperties.getInvalidRestoreKeyErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormField, String> validateAboutYourself(String aboutYourself) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        if (aboutYourself == null) {
            invalidFieldsMap.put(FormField.ABOUT_YOURSELF, resourcesProperties.getNullFieldErrorMsg());
        } else if (aboutYourself.length() > fieldsValidationProperties.getMaxAboutYourselfTextLength()) {
            String tooLongTextErrorMsg = String.format(resourcesProperties.getTooLongTextErrorMsg(), fieldsValidationProperties.getMaxAboutYourselfTextLength());
            invalidFieldsMap.put(FormField.ABOUT_YOURSELF, tooLongTextErrorMsg);
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormField, String> validateCity(String city) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        if (city == null) {
            invalidFieldsMap.put(FormField.CITY, resourcesProperties.getNullFieldErrorMsg());
        } else if (!city.matches(fieldsValidationProperties.getCityRegEx())) {
            invalidFieldsMap.put(FormField.CITY, resourcesProperties.getForbiddenCityNameErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormField, String> validateSchool(String school) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        if (school == null) {
            invalidFieldsMap.put(FormField.SCHOOL, resourcesProperties.getNullFieldErrorMsg());
        } else if (!school.matches(fieldsValidationProperties.getSchoolRegEx())) {
            invalidFieldsMap.put(FormField.SCHOOL, resourcesProperties.getForbiddenSchoolNameErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormField, String> validateUniversity(String university) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        if (university == null) {
            invalidFieldsMap.put(FormField.UNIVERSITY, resourcesProperties.getNullFieldErrorMsg());
        } else if (!university.matches(fieldsValidationProperties.getUniversityRegEx())) {
            invalidFieldsMap.put(FormField.UNIVERSITY, resourcesProperties.getForbiddenUniversityNameErrorMsg());
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormField, String> validatePostText(String postText) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        if (postText != null && postText.length() > fieldsValidationProperties.getMaxPostTextLength()) {
            String tooLongTextErrorMsg = String.format(resourcesProperties.getTooLongTextErrorMsg(), fieldsValidationProperties.getMaxPostTextLength());
            invalidFieldsMap.put(FormField.POST_TEXT, tooLongTextErrorMsg);
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormField, String> validateImage(MultipartFile image) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        if (image != null) {
            if (image.getSize() > fieldsValidationProperties.getMaxImageSize() * BYTES_IN_MB) {
                String tooLargeImageSizeErrorMsg = String.format(resourcesProperties.getTooLargeImageSizeErrorMsg(), fieldsValidationProperties.getMaxImageSize());
                invalidFieldsMap.put(FormField.IMAGE, tooLargeImageSizeErrorMsg);
            } else if (!getSplitValues(fieldsValidationProperties.getAllowedImageTypes()).contains(image.getContentType())) {
                String wrongImageTypeErrorMsg = String.format(resourcesProperties.getWrongImageTypeErrorMsg(), fieldsValidationProperties.getAllowedImageTypes());
                invalidFieldsMap.put(FormField.IMAGE, wrongImageTypeErrorMsg);
            }
        }
        return invalidFieldsMap;
    }

    @Override
    public Map<FormField, String> validateChatMessageText(String messageText) {
        Map<FormField, String> invalidFieldsMap = new HashMap<>();
        if (messageText == null) {
            invalidFieldsMap.put(FormField.CHAT_MESSAGE_TEXT, resourcesProperties.getNullFieldErrorMsg());
        } else if (messageText.length() > fieldsValidationProperties.getMaxChatMessageTextLength()) {
            String tooLongTextErrorMsg = String.format(resourcesProperties.getTooLongChatMessageTextErrorMsg(), fieldsValidationProperties.getMaxChatMessageTextLength());
            invalidFieldsMap.put(FormField.CHAT_MESSAGE_TEXT, tooLongTextErrorMsg);
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
