package andrew.project.socialNetwork.backend.api.libraries;

import andrew.project.socialNetwork.backend.api.dtos.FormStatusDto;
import andrew.project.socialNetwork.backend.api.dtos.RegFormDto;
import andrew.project.socialNetwork.backend.api.dtos.ResetPasswordRequestDto;
import andrew.project.socialNetwork.backend.api.dtos.WebSocketSessionKeyDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthLib {

    FormStatusDto registration(RegFormDto regFormDto);

    FormStatusDto deleteAccount(String password);

    boolean confirm(String key);

    FormStatusDto restore(String email);

    FormStatusDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);

    void refreshToken(HttpServletRequest request, HttpServletResponse response);

    WebSocketSessionKeyDto getWebSocketSessionKey();

}
