package andrew.project.socialNetwork.backend.api.dtos;

import andrew.project.socialNetwork.backend.api.constants.FormFields;
import andrew.project.socialNetwork.backend.api.constants.StatusCode;
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
public class FormStatusDto {
    private StatusCode status;
    private Map<FormFields, String> invalidFieldsMap = new HashMap<>();

}
