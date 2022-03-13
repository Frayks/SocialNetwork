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
    private String forbiddenFirstNameText;
    private String forbiddenLastNameText;
    private String invalidEmailText;
    private String busyEmailText;
    private String forbiddenUsernameText;
    private String reservedUsernameText;
    private String busyUsernameText;
    private String forbiddenPasswordText;
    private String wrongDateOfBirthText;
    private String sexUnknownText;

    private String confirmEmailSubjectText;
    private String confirmEmailBodyText;
}
