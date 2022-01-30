package andrew.project.socialNetwork.backend.api.libraries;

import andrew.project.socialNetwork.backend.api.dtos.UserProfileInfoDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MainLib {

    void addErrorToResponse(HttpServletResponse response, int status, String errorMessage) throws IOException;

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    UserProfileInfoDto getUserProfileInfo(String username);

}
