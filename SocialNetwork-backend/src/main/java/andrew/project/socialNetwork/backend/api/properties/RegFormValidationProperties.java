package andrew.project.socialNetwork.backend.api.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Setter
@Getter
@Configuration
@PropertySource("classpath:regFormValidation.properties")
@ConfigurationProperties(prefix = "reg-form-validation")
public class RegFormValidationProperties {
    private String firstNameRegEx;
    private String lastNameRegEx;
    private String usernameRegEx;
    private String reservedUsernames;
    private String separator;
    private String passwordRegEx;
    private int maxDateDiff;

}
