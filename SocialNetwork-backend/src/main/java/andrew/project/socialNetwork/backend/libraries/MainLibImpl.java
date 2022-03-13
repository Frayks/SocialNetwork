package andrew.project.socialNetwork.backend.libraries;

import andrew.project.socialNetwork.backend.api.constants.AddToFriendsStatusCode;
import andrew.project.socialNetwork.backend.api.constants.RegStatusCode;
import andrew.project.socialNetwork.backend.api.constants.RoleName;
import andrew.project.socialNetwork.backend.api.constants.TokenType;
import andrew.project.socialNetwork.backend.api.dtos.*;
import andrew.project.socialNetwork.backend.api.entities.*;
import andrew.project.socialNetwork.backend.api.libraries.MainLib;
import andrew.project.socialNetwork.backend.api.mappers.Mapper;
import andrew.project.socialNetwork.backend.api.properties.ImageStorageProperties;
import andrew.project.socialNetwork.backend.api.properties.SystemProperties;
import andrew.project.socialNetwork.backend.api.services.*;
import andrew.project.socialNetwork.backend.security.JwtProvider;
import andrew.project.socialNetwork.backend.validators.RegFormValidator;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
public class MainLibImpl implements MainLib {

    private static final Logger LOGGER = LogManager.getLogger(MainLibImpl.class);

    private UserService userService;
    private UserPhotoService userPhotoService;
    private UserPostService userPostService;
    private FriendsService friendsService;
    private ImageStorageService imageStorageService;
    private RegistrationService registrationService;
    private RegistrationRequestService registrationRequestService;

    private JwtProvider jwtProvider;
    private RegFormValidator regFormValidator;

    private ImageStorageProperties imageStorageProperties;
    private SystemProperties systemProperties;


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
            addErrorToResponse(response, HttpStatus.FORBIDDEN.value(), e.getMessage());
        }
    }

    @Override
    public RegStatusDto registration(RegFormDto regFormDto) {
        RegStatusDto regStatusDto = regFormValidator.validate(regFormDto);
        if (regStatusDto.getStatus().equals(RegStatusCode.SUCCESS)) {
            registrationService.register(regFormDto);
        }
        return regStatusDto;
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
    public UserProfileInfoDto getUserProfileInfo(String username) {
        try {
            User user = userService.findByUsername(username);
            if (user != null) {
                List<UserPhoto> userPhotoList = userPhotoService.findByUserId(user.getId());
                List<UserPost> userPostList = userPostService.findByUserIdOrderByCreationTimeDesc(user.getId());
                org.springframework.security.core.userdetails.User userRequester = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User userRequesterDbi = userService.findByUsername(userRequester.getUsername());
                List<Friends> friendsList = friendsService.findFriends(user.getId(), 6);
                List<Long> friendsIdList = getFriendsIdList(user.getId(), friendsList);
                List<User> userFriendList = userService.findByIds(friendsIdList);
                Friends friends = friendsService.checkIfFriends(userRequesterDbi.getId(), user.getId());
                return Mapper.mapToUserProfileInfoDto(user, userPhotoList, userPostList, userFriendList, friends, imageStorageProperties);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    @Override
    public UserFriendsInfoDto getUserFriendsInfo(String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            List<Friends> friendsList = friendsService.findFriends(user.getId());
            List<Long> friendsIdList = getFriendsIdList(user.getId(), friendsList);
            List<User> userFriendList = userService.findByIds(friendsIdList);
            List<User> userFriendRequestList = null;
            org.springframework.security.core.userdetails.User userdetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (username.equals(userdetails.getUsername())) {
                List<Friends> friendRequestList = friendsService.findRequestsToFriends(user.getId());
                List<Long> friendRequestsIdList = getFriendRequestsIdList(friendRequestList);
                userFriendRequestList = userService.findByIds(friendRequestsIdList);
            }
            return Mapper.mapToUserFriendsInfoDto(user, userFriendList, userFriendRequestList, imageStorageProperties);
        }
        return null;
    }

    @Override
    public MenuDataDto getMenuData() {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(user.getUsername());
        MenuDataDto menuDataDto = new MenuDataDto();
        menuDataDto.setNumOfMessages(0);
        menuDataDto.setNumOfRequestsToFriends(friendsService.findNumOfRequestsToFriends(userDbi.getId()));
        return menuDataDto;
    }

    @Override
    public NewsDto getNews(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return null;
        }
        List<Friends> friendsList = friendsService.findFriends(user.getId());
        List<Long> userIdList = getFriendsIdList(user.getId(), friendsList);
        userIdList.add(user.getId());
        List<User> userList = userService.findByIds(userIdList);
        List<UserPost> userPostList = userPostService.findByUserIdsOrderByCreationTimeDesc(userIdList);
        return Mapper.mapToNewsDto(userList, userPostList, imageStorageProperties);
    }

    @Override
    public UserPhotoDto addPhoto(MultipartFile file) {
        try {
            SaveImageResponseDto response = imageStorageService.saveImage(file);
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userDbi = userService.findByUsername(user.getUsername());
            UserPhoto userPhoto = new UserPhoto();
            userPhoto.setUserId(userDbi.getId());
            userPhoto.setName(response.getName());
            userPhoto = userPhotoService.save(userPhoto);
            return Mapper.mapToPhotoDto(userPhoto, imageStorageProperties);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    @Override
    public int deletePhoto(Long photoId) {
        try {
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userDbi = userService.findByUsername(user.getUsername());
            List<UserPhoto> userPhotoList = userPhotoService.findByIdAndUserId(photoId, userDbi.getId());
            if (userPhotoList.size() != 1) {
                return 0;
            }
            String photoName = userPhotoList.get(0).getName();
            imageStorageService.deleteImage(photoName);
            return userPhotoService.deleteByIdAndUserId(photoId, userDbi.getId());
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return 0;
    }

    @Override
    public UserPostDto createPost(MultipartFile file, String text) {
        try {
            if (file != null || text != null) {
                SaveImageResponseDto response = null;
                if (file != null) {
                    response = imageStorageService.saveImage(file);
                }
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User userDbi = userService.findByUsername(user.getUsername());
                UserPost userPost = new UserPost();
                userPost.setUserId(userDbi.getId());
                userPost.setText(text);
                if (response != null) {
                    userPost.setPhotoName(response.getName());
                }
                userPost = userPostService.save(userPost);
                return Mapper.mapToUserPostDto(userPost, imageStorageProperties);
            }

        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    @Override
    public int deletePost(Long postId) {
        try {
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userDbi = userService.findByUsername(user.getUsername());
            List<UserPost> userPostList = userPostService.findByIdAndUserId(postId, userDbi.getId());
            if (userPostList.size() != 1) {
                return 0;
            }
            String photoName = userPostList.get(0).getPhotoName();
            imageStorageService.deleteImage(photoName);
            return userPostService.deleteByIdAndUserId(postId, userDbi.getId());
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return 0;
    }

    @Override
    public AddToFriendsStatusCode createFriendRequest(Long userId) {
        try {
            org.springframework.security.core.userdetails.User userRequester = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userRequesterDbi = userService.findByUsername(userRequester.getUsername());
            if (userRequesterDbi.getId().equals(userId)) {
                return AddToFriendsStatusCode.BAD_REQUEST;
            }
            User user = userService.findById(userId);
            if (user == null) {
                return AddToFriendsStatusCode.BAD_REQUEST;
            }
            Friends friends = friendsService.checkIfFriends(userRequesterDbi.getId(), userId);
            if (friends != null) {
                if (friends.getAccepted()) {
                    return AddToFriendsStatusCode.BAD_REQUEST;
                } else {
                    if (Objects.equals(friends.getSecondUserId(), userRequesterDbi.getId())) {
                        friends.setAccepted(true);
                        friendsService.save(friends);
                        return AddToFriendsStatusCode.ADDED;
                    } else {
                        return AddToFriendsStatusCode.BAD_REQUEST;
                    }
                }
            }
            friends = new Friends();
            friends.setFirstUserId(userRequesterDbi.getId());
            friends.setSecondUserId(userId);
            friendsService.save(friends);
            return AddToFriendsStatusCode.REQUEST_CREATED;
        } catch (Exception e) {
            LOGGER.error(e);
            return AddToFriendsStatusCode.BAD_REQUEST;
        }
    }

    @Override
    public boolean cancelFriendRequest(Long userId) {
        try {
            org.springframework.security.core.userdetails.User userRequester = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userRequesterDbi = userService.findByUsername(userRequester.getUsername());
            if (userRequesterDbi.getId().equals(userId)) {
                return false;
            }
            User user = userService.findById(userId);
            if (user == null) {
                return false;
            }
            Friends friends = friendsService.checkIfFriends(userRequesterDbi.getId(), userId);
            if (friends != null && Objects.equals(friends.getSecondUserId(), userId)) {
                friendsService.delete(friends);
                return true;
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }

    @Override
    public void deleteFriend(Long userId) {
        try {
            org.springframework.security.core.userdetails.User userRequester = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userRequesterDbi = userService.findByUsername(userRequester.getUsername());
            if (userRequesterDbi.getId().equals(userId)) {
                return;
            }
            User user = userService.findById(userId);
            if (user == null) {
                return;
            }
            Friends friends = friendsService.checkIfFriends(userRequesterDbi.getId(), userId);
            if (friends != null && friends.getAccepted()) {
                friendsService.delete(friends);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void agreeFriendRequest(Long userId) {
        try {
            org.springframework.security.core.userdetails.User userRequester = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userRequesterDbi = userService.findByUsername(userRequester.getUsername());
            if (userRequesterDbi.getId().equals(userId)) {
                return;
            }
            User user = userService.findById(userId);
            if (user == null) {
                return;
            }
            Friends friends = friendsService.checkIfFriends(userRequesterDbi.getId(), userId);
            if (friends != null && Objects.equals(friends.getSecondUserId(), userRequesterDbi.getId())) {
                friends.setAccepted(true);
                friendsService.save(friends);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void rejectFriendRequest(Long userId) {
        try {
            org.springframework.security.core.userdetails.User userRequester = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userRequesterDbi = userService.findByUsername(userRequester.getUsername());
            if (userRequesterDbi.getId().equals(userId)) {
                return;
            }
            User user = userService.findById(userId);
            if (user == null) {
                return;
            }
            Friends friends = friendsService.checkIfFriends(userRequesterDbi.getId(), userId);
            if (friends != null && Objects.equals(friends.getSecondUserId(), userRequesterDbi.getId())) {
                friendsService.delete(friends);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public void addErrorToResponse(HttpServletResponse response, int status, String errorMessage) {
        response.setStatus(status);
        Map<String, String> error = new HashMap<>();
        error.put("error_message", errorMessage);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private List<Long> getFriendsIdList(Long userId, List<Friends> friendsList) {
        List<Long> friendsIdList = new ArrayList<>();
        for (Friends friends : friendsList) {
            friendsIdList.add((Objects.equals(friends.getFirstUserId(), userId)) ? friends.getSecondUserId() : friends.getFirstUserId());
        }
        return friendsIdList;
    }

    private List<Long> getFriendRequestsIdList(List<Friends> friendsList) {
        List<Long> friendsIdList = new ArrayList<>();
        for (Friends friends : friendsList) {
            friendsIdList.add(friends.getFirstUserId());
        }
        return friendsIdList;
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
    public void setFriendsService(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Autowired
    public void setImageStorageService(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @Autowired
    public void setImageStorageProperties(ImageStorageProperties imageStorageProperties) {
        this.imageStorageProperties = imageStorageProperties;
    }

    @Autowired
    public void setSystemProperties(SystemProperties systemProperties) {
        this.systemProperties = systemProperties;
    }

    @Autowired
    public void setRegFormValidator(RegFormValidator regFormValidator) {
        this.regFormValidator = regFormValidator;
    }

    @Autowired
    public void setRegistrationService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Autowired
    public void setRegistrationRequestService(RegistrationRequestService registrationRequestService) {
        this.registrationRequestService = registrationRequestService;
    }
}
