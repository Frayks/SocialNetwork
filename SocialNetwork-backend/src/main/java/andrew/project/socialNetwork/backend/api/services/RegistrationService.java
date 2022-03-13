package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.dtos.RegFormDto;

public interface RegistrationService {

    void register(RegFormDto regFormDto);

}
