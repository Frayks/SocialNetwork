package andrew.project.socialNetwork.backend.api.dtos;

import andrew.project.socialNetwork.backend.api.constants.FormField;
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
    private StatusCode status = StatusCode.FAILURE;
    private Map<FormField, String> invalidFieldsMap = new HashMap<>();

}
