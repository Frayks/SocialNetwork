package andrew.project.socialNetwork.backend.libraries;

import andrew.project.socialNetwork.backend.api.constants.TokenType;
import andrew.project.socialNetwork.backend.api.dtos.UserProfileInfoDto;
import andrew.project.socialNetwork.backend.api.entities.User;
import andrew.project.socialNetwork.backend.api.entities.UserPhoto;
import andrew.project.socialNetwork.backend.api.entities.UserPost;
import andrew.project.socialNetwork.backend.api.libraries.MainLib;
import andrew.project.socialNetwork.backend.api.mappers.UserMapper;
import andrew.project.socialNetwork.backend.api.services.UserPhotoService;
import andrew.project.socialNetwork.backend.api.services.UserPostService;
import andrew.project.socialNetwork.backend.api.services.UserService;
import andrew.project.socialNetwork.backend.security.JwtProvider;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainLibImpl implements MainLib {

    private static final Logger LOGGER = LogManager.getLogger(MainLibImpl.class);

    private UserService userService;
    private UserPhotoService userPhotoService;
    private UserPostService userPostService;
    private JwtProvider jwtProvider;

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String refreshToken = jwtProvider.resolveToken(request);
            if (refreshToken == null) {
                throw new Exception("Refresh token is missing!");
            }
            DecodedJWT decodedJWT = jwtProvider.verifyToken(refreshToken, TokenType.REFRESH_TOKEN);
            String username = decodedJWT.getSubject();
            User user = userService.findByUsername(username);
            List<String> roles = new ArrayList<>();
            user.getRoles().forEach(role -> roles.add(role.getRoleName().name()));
            String accessToken = jwtProvider.createAccessToken(user.getUsername(), roles);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", refreshToken);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            addErrorToResponse(response, HttpStatus.FORBIDDEN.value(), e.getMessage());
        }
    }

    @Override
    public UserProfileInfoDto getUserProfileInfo(String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            List<UserPhoto> userPhotoList = userPhotoService.findByUserId(user.getId());
            List<UserPost> userPostList = userPostService.findByUserId(user.getId());
            return UserMapper.mapToUserProfileInfoDto(user, userPhotoList, userPostList);
        }
        return null;
    }

    @Override
    public void addErrorToResponse(HttpServletResponse response, int status, String errorMessage) throws IOException {
        response.setStatus(status);
        Map<String, String> error = new HashMap<>();
        error.put("error_message", errorMessage);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserPhotoService(UserPhotoService userPhotoService) {
        this.userPhotoService = userPhotoService;
    }

    @Autowired
    public void setUserPostService(UserPostService userPostService) {
        this.userPostService = userPostService;
    }

    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

}
