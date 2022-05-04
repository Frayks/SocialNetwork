package andrew.project.socialNetwork.backend.api.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Setter
@Getter
@Configuration
@PropertySource(value = "classpath:resources.properties", encoding = "UTF-8")
@ConfigurationProperties(prefix = "resources")
public class ResourcesProperties {
    private String forbiddenFirstNameErrorMsg;
    private String forbiddenLastNameErrorMsg;
    private String invalidEmailErrorMsg;
    private String busyEmailErrorMsg;
    private String missingAccountByEmailErrorMsg;
    private String forbiddenUsernameErrorMsg;
    private String reservedUsernameErrorMsg;
    private String busyUsernameErrorMsg;
    private String forbiddenPasswordErrorMsg;
    private String wrongPasswordErrorMsg;
    private String wrongDateOfBirthErrorMsg;
    private String sexUnknownErrorMsg;
    private String invalidRestoreKeyErrorMsg;
    private String tooLongTextErrorMsg;
    private String tooLongChatMessageTextErrorMsg;
    private String tooLargeImageSizeErrorMsg;
    private String wrongImageTypeErrorMsg;
    private String forbiddenCityNameErrorMsg;
    private String forbiddenSchoolNameErrorMsg;
    private String forbiddenUniversityNameErrorMsg;
    private String oneFilledFieldIsRequiredErrorMsg;
    private String nullFieldErrorMsg;
    private String zeroValueErrorMsg;

    private String confirmMailSubjectText;
    private String confirmMailBodyText;
    private String restoreMailSubjectText;
    private String restoreMailBodyText;

}
