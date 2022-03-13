package andrew.project.socialNetwork.backend.validators;

import andrew.project.socialNetwork.backend.api.constants.RegFormField;
import andrew.project.socialNetwork.backend.api.constants.RegStatusCode;
import andrew.project.socialNetwork.backend.api.constants.Sex;
import andrew.project.socialNetwork.backend.api.dtos.RegFormDto;
import andrew.project.socialNetwork.backend.api.dtos.RegStatusDto;
import andrew.project.socialNetwork.backend.api.entities.RegistrationRequest;
import andrew.project.socialNetwork.backend.api.properties.RegFormValidationProperties;
import andrew.project.socialNetwork.backend.api.properties.ResourcesProperties;
import andrew.project.socialNetwork.backend.api.services.RegistrationRequestService;
import andrew.project.socialNetwork.backend.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RegFormValidator {

    private UserService userService;
    private RegistrationRequestService registrationRequestService;
    private RegFormValidationProperties regFormValidationProperties;
    private ResourcesProperties resourcesProperties;

    public RegStatusDto validate(RegFormDto regFormDto) {
        Map<RegFormField, String> invalidFieldsMap = new HashMap<>();

        validateFirstName(invalidFieldsMap, regFormDto.getFirstName());
        validateLastName(invalidFieldsMap, regFormDto.getLastName());
        validateEmail(invalidFieldsMap, regFormDto.getEmail());
        validateUsername(invalidFieldsMap, regFormDto.getUsername(), regFormDto.getEmail());
        validatePassword(invalidFieldsMap, regFormDto.getPassword());
        validateDate(invalidFieldsMap, regFormDto.getDayOfBirth(), regFormDto.getMonthOfBirth(), regFormDto.getYearOfBirth());
        validateSex(invalidFieldsMap, regFormDto.getSex());

        RegStatusDto regStatusDto = new RegStatusDto();
        if (invalidFieldsMap.size() > 0) {
            regStatusDto.setStatus(RegStatusCode.FAILURE);
            regStatusDto.setInvalidFieldsMap(invalidFieldsMap);
        } else {
            regStatusDto.setStatus(RegStatusCode.SUCCESS);
        }
        return regStatusDto;
    }

    private Map<RegFormField, String> validateFirstName(Map<RegFormField, String> invalidFieldsMap, String firstName) {
        if (!firstName.matches(regFormValidationProperties.getFirstNameRegEx())) {
            invalidFieldsMap.put(RegFormField.FIRST_NAME, resourcesProperties.getForbiddenFirstNameText());
        }
        return invalidFieldsMap;
    }

    private Map<RegFormField, String> validateLastName(Map<RegFormField, String> invalidFieldsMap, String lastName) {
        if (!lastName.matches(regFormValidationProperties.getLastNameRegEx())) {
            invalidFieldsMap.put(RegFormField.LAST_NAME, resourcesProperties.getForbiddenLastNameText());
        }
        return invalidFieldsMap;
    }

    private Map<RegFormField, String> validateEmail(Map<RegFormField, String> invalidFieldsMap, String email) {
        if (!email.contains("@")) {
            invalidFieldsMap.put(RegFormField.EMAIL, resourcesProperties.getInvalidEmailText());
        } else if (userService.findByEmail(email) != null) {
            invalidFieldsMap.put(RegFormField.EMAIL, resourcesProperties.getBusyEmailText());
        }
        return invalidFieldsMap;
    }

    private Map<RegFormField, String> validateUsername(Map<RegFormField, String> invalidFieldsMap, String username, String email) {
        if (!username.matches(regFormValidationProperties.getUsernameRegEx())) {
            invalidFieldsMap.put(RegFormField.USERNAME, resourcesProperties.getForbiddenUsernameText());
        } else if (getSplitReservedUsernames().contains(username)) {
            invalidFieldsMap.put(RegFormField.USERNAME, resourcesProperties.getReservedUsernameText());
        } else if (userService.findByUsername(username) != null) {
            invalidFieldsMap.put(RegFormField.USERNAME, resourcesProperties.getBusyUsernameText());
        } else {
            RegistrationRequest registrationRequest = registrationRequestService.findByUsername(username);
            if (registrationRequest != null && !registrationRequest.getEmail().equals(email)) {
                invalidFieldsMap.put(RegFormField.USERNAME, resourcesProperties.getBusyUsernameText());
            }
        }
        return invalidFieldsMap;
    }

    private Map<RegFormField, String> validatePassword(Map<RegFormField, String> invalidFieldsMap, String password) {
        if (!password.matches(regFormValidationProperties.getPasswordRegEx())) {
            invalidFieldsMap.put(RegFormField.PASSWORD, resourcesProperties.getForbiddenPasswordText());
        }
        return invalidFieldsMap;
    }

    private Map<RegFormField, String> validateDate(Map<RegFormField, String> invalidFieldsMap, int dayOfBirth, int monthOfBirth, int yearOfBirth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(yearOfBirth, monthOfBirth - 1, dayOfBirth);
        Calendar calendarNow = Calendar.getInstance();
        Date date = new Date();
        if (calendar.getTimeInMillis() > calendarNow.getTimeInMillis() || calendarNow.get(Calendar.YEAR) - calendar.get(Calendar.YEAR) > regFormValidationProperties.getMaxDateDiff()) {
            invalidFieldsMap.put(RegFormField.DATE_OF_BIRTH, resourcesProperties.getWrongDateOfBirthText());
        }
        return invalidFieldsMap;
    }

    private Map<RegFormField, String> validateSex(Map<RegFormField, String> invalidFieldsMap, String sex) {
        if (!(sex.equals(Sex.MALE.name()) || sex.equals(Sex.FEMALE.name()))) {
            invalidFieldsMap.put(RegFormField.SEX, resourcesProperties.getSexUnknownText());
        }
        return invalidFieldsMap;
    }

    private List<String> getSplitReservedUsernames() {
        return Arrays.asList(regFormValidationProperties.getReservedUsernames().split(regFormValidationProperties.getSeparator()));
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
    public void setRegFormValidationProperties(RegFormValidationProperties regFormValidationProperties) {
        this.regFormValidationProperties = regFormValidationProperties;
    }

    @Autowired
    public void setResourcesProperties(ResourcesProperties resourcesProperties) {
        this.resourcesProperties = resourcesProperties;
    }
}
