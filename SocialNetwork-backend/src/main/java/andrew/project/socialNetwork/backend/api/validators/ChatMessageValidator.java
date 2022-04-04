package andrew.project.socialNetwork.backend.api.validators;

import andrew.project.socialNetwork.backend.api.data.NewChatMessage;
import andrew.project.socialNetwork.backend.api.dtos.FormStatusDto;

public interface ChatMessageValidator {

    FormStatusDto validate(NewChatMessage chatMessage);

}
