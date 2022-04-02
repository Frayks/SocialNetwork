package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.dtos.RegFormDto;
import andrew.project.socialNetwork.backend.api.entities.RegistrationRequest;
import andrew.project.socialNetwork.backend.api.mappers.Mapper;
import andrew.project.socialNetwork.backend.api.properties.ResourcesProperties;
import andrew.project.socialNetwork.backend.api.properties.SystemProperties;
import andrew.project.socialNetwork.backend.api.services.EmailSenderService;
import andrew.project.socialNetwork.backend.api.services.RegistrationRequestService;
import andrew.project.socialNetwork.backend.api.services.RegistrationService;
import andrew.project.socialNetwork.backend.api.utils.GeneratorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;

@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    private static final Logger LOGGER = LogManager.getLogger(RegistrationServiceImpl.class);

    private static final String CONFIRM_ENDPOINT = "confirm";

    private RegistrationRequestService registrationRequestService;
    private EmailSenderService emailSenderService;
    private SystemProperties systemProperties;
    private ResourcesProperties resourcesProperties;

    @Async
    @Override
    public void register(RegFormDto regFormDto) {
        try {
            String confirmKey = GeneratorUtil.genRandStr(GeneratorUtil.DEFAULT_KEY_LENGTH);
            String confirmUri = UriComponentsBuilder
                    .fromUriString(systemProperties.getClientServerUrl())
                    .pathSegment(CONFIRM_ENDPOINT)
                    .pathSegment(confirmKey)
                    .build()
                    .toString();
            emailSenderService.sendEmail(regFormDto.getEmail(), resourcesProperties.getConfirmMailSubjectText(), String.format(resourcesProperties.getConfirmMailBodyText(), confirmUri));
            registrationRequestService.deleteByEmail(regFormDto.getEmail());
            RegistrationRequest registrationRequest = Mapper.mapRegistrationRequest(regFormDto);
            registrationRequest.setCreationTime(new Timestamp(System.currentTimeMillis()));
            registrationRequest.setConfirmKey(confirmKey);
            registrationRequestService.save(registrationRequest);
        } catch (Exception e) {
            LOGGER.debug(e);
        }
    }

    @Autowired
    public void setRegistrationRequestService(RegistrationRequestService registrationRequestService) {
        this.registrationRequestService = registrationRequestService;
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
