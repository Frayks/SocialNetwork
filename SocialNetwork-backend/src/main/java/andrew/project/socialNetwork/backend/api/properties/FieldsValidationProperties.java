package andrew.project.socialNetwork.backend.api.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Setter
@Getter
@Configuration
@PropertySource("classpath:fieldsValidation.properties")
@ConfigurationProperties(prefix = "fields-validation")
public class FieldsValidationProperties {
    private String firstNameRegEx;
    private String lastNameRegEx;
    private String usernameRegEx;
    private String passwordRegEx;
    private String cityRegEx;
    private String schoolRegEx;
    private String universityRegEx;
    private String reservedUsernames;
    private String allowedImageTypes;
    private String separator;
    private int maxImageSize;
    private int maxPostTextLength;
    private int maxAboutYourselfTextLength;
    private int maxChatMessageTextLength;
    private int maxDateDiff;

}
