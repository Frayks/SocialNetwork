package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.entities.RestoreRequest;
import andrew.project.socialNetwork.backend.api.entities.User;
import andrew.project.socialNetwork.backend.api.properties.ResourcesProperties;
import andrew.project.socialNetwork.backend.api.properties.SystemProperties;
import andrew.project.socialNetwork.backend.api.services.EmailSenderService;
import andrew.project.socialNetwork.backend.api.services.RestoreRequestService;
import andrew.project.socialNetwork.backend.api.services.RestoreService;
import andrew.project.socialNetwork.backend.api.utils.GeneratorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class RestoreServiceImpl implements RestoreService {

    private static final Logger LOGGER = LogManager.getLogger(RestoreServiceImpl.class);

    private static final String RESET_PASSWORD_ENDPOINT = "resetPassword";

    private RestoreRequestService restoreRequestService;
    private EmailSenderService emailSenderService;
    private SystemProperties systemProperties;
    private ResourcesProperties resourcesProperties;

    @Async
    @Override
    public void restore(User user) {
        try {
            String restoreKey = GeneratorUtil.genRandStr(GeneratorUtil.DEFAULT_KEY_LENGTH);
            String restoreUri = UriComponentsBuilder
                    .fromUriString(systemProperties.getClientServerUrl())
                    .pathSegment(RESET_PASSWORD_ENDPOINT)
                    .pathSegment(restoreKey)
                    .build()
                    .toString();
            emailSenderService.sendEmail(user.getEmail(), resourcesProperties.getRestoreMailSubjectText(), String.format(resourcesProperties.getRestoreMailBodyText(), restoreUri));
            restoreRequestService.deleteByUserId(user.getId());
            RestoreRequest restoreRequest = new RestoreRequest();
            restoreRequest.setUserId(user.getId());
            restoreRequest.setRestoreKey(restoreKey);
            restoreRequestService.save(restoreRequest);
        } catch (Exception e) {
            LOGGER.debug(e);
        }
    }

    @Autowired
    public void setRestoreRequestService(RestoreRequestService restoreRequestService) {
        this.restoreRequestService = restoreRequestService;
    }

    @Autowired
    public void setEmailSenderService(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @Autowired
    public void setSystemProperties(SystemProperties systemProperties) {
        this.systemProperties = systemProperties;
    }

    @Autowired
    public void setResourcesProperties(ResourcesProperties resourcesProperties) {
        this.resourcesProperties = resourcesProperties;
    }
}
