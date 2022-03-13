package andrew.project.socialNetwork.backend.api.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Setter
@Getter
@Configuration
@PropertySource("classpath:imageStorage.properties")
@ConfigurationProperties(prefix = "image-storage")
public class ImageStorageProperties {
    private String url;
    private String saveImageEndpoint;
    private String deleteImageEndpoint;
    private String getImageEndpoint;
    private String username;
    private String password;

}
