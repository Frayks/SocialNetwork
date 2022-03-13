package andrew.project.socialNetwork.backend.api.dtos;

import andrew.project.socialNetwork.backend.api.constants.RegFormField;
import andrew.project.socialNetwork.backend.api.constants.RegStatusCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RegStatusDto {
    private RegStatusCode status;
    private Map<RegFormField, String> invalidFieldsMap = new HashMap<>();

}
