package andrew.project.socialNetwork.backend.api.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Setter
@Getter
@Configuration
@PropertySource("classpath:system.properties")
@ConfigurationProperties(prefix = "system")
public class SystemProperties {
    private String serverUrl;
    private String clientServerUrl;
    private String defaultUserAvatarName;
    private boolean initMainData;
}
