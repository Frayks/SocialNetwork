package andrew.project.socialNetwork.backend.libraries;

import andrew.project.socialNetwork.backend.api.constants.RoleName;
import andrew.project.socialNetwork.backend.api.constants.StatusCode;
import andrew.project.socialNetwork.backend.api.constants.TokenType;
import andrew.project.socialNetwork.backend.api.dtos.FormStatusDto;
import andrew.project.socialNetwork.backend.api.dtos.RegFormDto;
import andrew.project.socialNetwork.backend.api.dtos.ResetPasswordRequestDto;
import andrew.project.socialNetwork.backend.api.dtos.WebSocketSessionKeyDto;
import andrew.project.socialNetwork.backend.api.entities.*;
import andrew.project.socialNetwork.backend.api.handlers.ChatMessagesHandler;
import andrew.project.socialNetwork.backend.api.libraries.AuthLib;
import andrew.project.socialNetwork.backend.api.mappers.Mapper;
import andrew.project.socialNetwork.backend.api.properties.ImageStorageProperties;
import andrew.project.socialNetwork.backend.api.properties.SystemProperties;
import andrew.project.socialNetwork.backend.api.services.*;
import andrew.project.socialNetwork.backend.api.storages.WebSocketSessionsStorage;
import andrew.project.socialNetwork.backend.api.utils.CommonUtil;
import andrew.project.socialNetwork.backend.api.utils.GeneratorUtil;
import andrew.project.socialNetwork.backend.api.validators.*;
import andrew.project.socialNetwork.backend.security.JwtProvider;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AuthLibImpl implements AuthLib {

    private static final Logger LOGGER = LogManager.getLogger(AuthLibImpl.class);

    private UserService userService;
    private RestoreService restoreService;
    private FriendsService friendsService;
    private UserPostService userPostService;
    private PostLikeService postLikeService;
    private UserPhotoService userPhotoService;
    private PhotoLikeService photoLikeService;
    private ImageStorageService imageStorageService;
    private RegistrationService registrationService;
    private UserWsSessionService userWsSessionService;
    private RestoreRequestService restoreRequestService;
    private RegistrationRequestService registrationRequestService;

    private RestoreValidator restoreValidator;
    private RegFormValidator regFormValidator;
    private ResetPasswordValidator resetPasswordValidator;
    private DeleteAccountValidator deleteAccountValidator;

    private JwtProvider jwtProvider;
    private PasswordEncoder passwordEncoder;
    private SystemProperties systemProperties;
    private WebSocketSessionsStorage webSocketSessionsStorage;


    @Override
    public FormStatusDto registration(RegFormDto regFormDto) {
        FormStatusDto formStatusDto = regFormValidator.validate(regFormDto);
        if (formStatusDto.getStatus().equals(StatusCode.SUCCESS)) {
            registrationService.register(regFormDto);
        }
        return formStatusDto;
    }

    @Override
    public FormStatusDto deleteAccount(String password) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FormStatusDto formStatusDto = deleteAccountValidator.validate(password, user.getPassword());
        if (formStatusDto.getStatus().equals(StatusCode.SUCCESS)) {
            User userDbi = userService.findByUsername(user.getUsername());
            if (userDbi != null) {

                List<UserWsSession> userWsSessionList = userWsSessionService.findByUserId(userDbi.getId());
                for (UserWsSession userWsSession : userWsSessionList) {
                    webSocketSessionsStorage.remove(userWsSession.getSessionId());
                }
                userWsSessionService.deleteByUserId(userDbi.getId());

                List<UserPhoto> userPhotoList = userPhotoService.findByUserId(userDbi.getId());
                List<Long> photoIdList = CommonUtil.getPhotoIdList(userPhotoList);
                List<String> photoNameList = CommonUtil.getPhotoNameList(userPhotoList);
                photoLikeService.deleteByPhotoIdIn(photoIdList);
                userPhotoService.deleteByUserId(userDbi.getId());
                imageStorageService.deleteImageList(photoNameList);

                List<UserPost> userPostList = userPostService.findByUserId(userDbi.getId());
                List<Long> postIdList = CommonUtil.getPostIdList(userPostList);
                List<String> postPhotoNameList = CommonUtil.getPostPhotoNameList(userPostList);
                postLikeService.deleteByPostIdIn(postIdList);
                userPostService.deleteByUserId(userDbi.getId());
                imageStorageService.deleteImageList(postPhotoNameList);

                friendsService.deleteByUserId(userDbi.getId());

                userDbi.setUsername(GeneratorUtil.genRandStr(15));
                userDbi.setPassword("Deleted");
                userDbi.setFirstName("Deleted");
                userDbi.setLastName("Deleted");
                userDbi.setEmail("Deleted");
                userDbi.setDeleted(true);
                UserInfo userInfo = userDbi.getUserInfo();
                userInfo.setAvatarName(systemProperties.getDefaultUserAvatarName());
                userInfo.setDateOfBirth(null);
                userInfo.setSex(null);
                userInfo.setUniversity(null);
                userInfo.setCity(null);
                userInfo.setSchool(null);
                userInfo.setUniversity(null);
                userInfo.setAboutYourself(null);
                userService.save(userDbi);

            }
        }
        return formStatusDto;
    }

    @Override
    public boolean confirm(String key) {
        RegistrationRequest registrationRequest = registrationRequestService.findByConfirmKey(key);
        if (registrationRequest == null) {
            return false;
        }
        registrationRequestService.delete(registrationRequest);
        User user = Mapper.mapToUser(registrationRequest);
        user.getUserInfo().setAvatarName(systemProperties.getDefaultUserAvatarName());
        userService.save(user);
        userService.addRoleToUser(user.getUsername(), RoleName.USER);
        return true;
    }

    @Override
    public FormStatusDto restore(String email) {
        FormStatusDto formStatusDto = restoreValidator.validate(email);
        if (formStatusDto.getStatus().equals(StatusCode.SUCCESS)) {
            User user = userService.findByEmail(email);
            restoreService.restore(user);
        }
        return formStatusDto;
    }

    @Override
    public FormStatusDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        FormStatusDto formStatusDto = resetPasswordValidator.validate(resetPasswordRequestDto);
        if (formStatusDto.getStatus().equals(StatusCode.SUCCESS)) {
            RestoreRequest restoreRequest = restoreRequestService.findByRestoreKey(resetPasswordRequestDto.getRestoreKey());
            restoreRequestService.deleteByUserId(restoreRequest.getUserId());
            User user = userService.findById(restoreRequest.getUserId());
            if (user != null) {
                user.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));
                userService.save(user);
            }
        }
        return formStatusDto;
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
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
            CommonUtil.addErrorToResponse(response, HttpStatus.FORBIDDEN.value(), e.getMessage());
        }
    }

    @Override
    public WebSocketSessionKeyDto getWebSocketSessionKey() {
        org.springframework.security.core.userdetails.User userRequester = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(userRequester.getUsername());
        return new WebSocketSessionKeyDto(webSocketSessionsStorage.generateWebSocketRequestKey(userDbi.getId()));
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRestoreService(RestoreService restoreService) {
        this.restoreService = restoreService;
    }

    @Autowired
    public void setFriendsService(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @Autowired
    public void setUserPostService(UserPostService userPostService) {
        this.userPostService = userPostService;
    }

    @Autowired
    public void setPostLikeService(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @Autowired
    public void setUserPhotoService(UserPhotoService userPhotoService) {
        this.userPhotoService = userPhotoService;
    }

    @Autowired
    public void setPhotoLikeService(PhotoLikeService photoLikeService) {
        this.photoLikeService = photoLikeService;
    }

    @Autowired
    public void setImageStorageService(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @Autowired
    public void setRegistrationService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Autowired
    public void setUserWsSessionService(UserWsSessionService userWsSessionService) {
        this.userWsSessionService = userWsSessionService;
    }

    @Autowired
    public void setRestoreRequestService(RestoreRequestService restoreRequestService) {
        this.restoreRequestService = restoreRequestService;
    }

    @Autowired
    public void setRegistrationRequestService(RegistrationRequestService registrationRequestService) {
        this.registrationRequestService = registrationRequestService;
    }

    @Autowired
    public void setRestoreValidator(RestoreValidator restoreValidator) {
        this.restoreValidator = restoreValidator;
    }

    @Autowired
    public void setRegFormValidator(RegFormValidator regFormValidator) {
        this.regFormValidator = regFormValidator;
    }

    @Autowired
    public void setResetPasswordValidator(ResetPasswordValidator resetPasswordValidator) {
        this.resetPasswordValidator = resetPasswordValidator;
    }

    @Autowired
    public void setDeleteAccountValidator(DeleteAccountValidator deleteAccountValidator) {
        this.deleteAccountValidator = deleteAccountValidator;
    }

    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setSystemProperties(SystemProperties systemProperties) {
        this.systemProperties = systemProperties;
    }

    @Autowired
    public void setWebSocketSessionsStorage(WebSocketSessionsStorage webSocketSessionsStorage) {
        this.webSocketSessionsStorage = webSocketSessionsStorage;
    }

}
