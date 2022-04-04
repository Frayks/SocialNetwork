package andrew.project.socialNetwork.backend.libraries;

import andrew.project.socialNetwork.backend.api.constants.*;
import andrew.project.socialNetwork.backend.api.dtos.*;
import andrew.project.socialNetwork.backend.api.entities.*;
import andrew.project.socialNetwork.backend.api.handlers.ChatMessagesHandler;
import andrew.project.socialNetwork.backend.api.libraries.MainLib;
import andrew.project.socialNetwork.backend.api.mappers.Mapper;
import andrew.project.socialNetwork.backend.api.properties.ImageStorageProperties;
import andrew.project.socialNetwork.backend.api.properties.SystemProperties;
import andrew.project.socialNetwork.backend.api.services.*;
import andrew.project.socialNetwork.backend.api.storages.WebSocketSessionsStorage;
import andrew.project.socialNetwork.backend.api.validators.RegFormValidator;
import andrew.project.socialNetwork.backend.api.validators.ResetPasswordValidator;
import andrew.project.socialNetwork.backend.api.validators.SettingsValidator;
import andrew.project.socialNetwork.backend.security.JwtProvider;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

@Component
public class MainLibImpl implements MainLib {

    private static final Logger LOGGER = LogManager.getLogger(MainLibImpl.class);

    private UserService userService;
    private UserPhotoService userPhotoService;
    private UserPostService userPostService;
    private FriendsService friendsService;
    private ImageStorageService imageStorageService;
    private RegistrationRequestService registrationRequestService;
    private RegistrationService registrationService;
    private RestoreRequestService restoreRequestService;
    private RestoreService restoreService;
    private PhotoLikeService photoLikeService;
    private PostLikeService postLikeService;
    private UserChatService userChatService;
    private UserChatMessageService userChatMessageService;
    private UserWsSessionService userWsSessionService;

    private JwtProvider jwtProvider;
    private RegFormValidator regFormValidator;
    private ResetPasswordValidator resetPasswordValidator;
    private PasswordEncoder passwordEncoder;
    private SettingsValidator settingsValidator;
    private WebSocketSessionsStorage webSocketSessionsStorage;
    private ChatMessagesHandler chatMessagesHandler;

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
    public FormStatusDto registration(RegFormDto regFormDto) {
        FormStatusDto formStatusDto = regFormValidator.validate(regFormDto);
        if (formStatusDto.getStatus().equals(StatusCode.SUCCESS)) {
            registrationService.register(regFormDto);
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
    public boolean restore(String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return false;
        }
        restoreService.restore(user);
        return true;
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
    public UserProfileInfoDto getUserProfileInfo(String username) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(user.getUsername());
        try {
            User targetUser = userService.findByUsername(username);
            if (targetUser != null) {
                int numOfPosts = userPostService.countByUserId(targetUser.getId());
                List<UserPhoto> userPhotoList = userPhotoService.findByUserIdOrderByLoadTimeDesc(targetUser.getId());
                List<Long> photosIdList = getPhotosIdList(userPhotoList);
                List<PhotoLike> photoLikeList = photoLikeService.findByPhotoIdInAndUserId(photosIdList, userDbi.getId());
                org.springframework.security.core.userdetails.User userRequester = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User userRequesterDbi = userService.findByUsername(userRequester.getUsername());
                List<Friends> friendsList = friendsService.findFriends(targetUser.getId(), 9);
                List<Long> friendsIdList = getFriendsIdList(targetUser.getId(), friendsList);
                List<User> userFriendList = userService.findByIdIn(friendsIdList);
                Friends friends = friendsService.checkIfFriends(userRequesterDbi.getId(), targetUser.getId());
                boolean online = targetUser.getId().equals(userDbi.getId()) || userWsSessionService.findByUserId(targetUser.getId()).size() > 0;
                UserProfileInfoDto userProfileInfoDto = Mapper.mapToUserProfileInfoDto(targetUser, online, userPhotoList, photoLikeList, userFriendList, numOfPosts, friends, imageStorageProperties);
                for (ShortUserInfoDto shortUserInfoDto : userProfileInfoDto.getUserFriendList()) {
                    if (shortUserInfoDto.getId().equals(userDbi.getId())) {
                        shortUserInfoDto.setOnline(true);
                    } else {
                        shortUserInfoDto.setOnline(userWsSessionService.findByUserId(shortUserInfoDto.getId()).size() > 0);
                    }
                }
                return userProfileInfoDto;
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
            List<User> userFriendList = userService.findByIdIn(friendsIdList);
            List<User> userFriendRequestList = null;
            org.springframework.security.core.userdetails.User userdetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (username.equals(userdetails.getUsername())) {
                List<Friends> friendRequestList = friendsService.findRequestsToFriends(user.getId());
                List<Long> friendRequestsIdList = getFriendRequestsIdList(friendRequestList);
                userFriendRequestList = userService.findByIdIn(friendRequestsIdList);
            }
            UserFriendsInfoDto userProfileInfoDto = Mapper.mapToUserFriendsInfoDto(user, userFriendList, userFriendRequestList, imageStorageProperties);
            for (ShortUserInfoDto shortUserInfoDto : userProfileInfoDto.getUserFriendList()) {
                if (shortUserInfoDto.getId().equals(user.getId())) {
                    shortUserInfoDto.setOnline(true);
                } else {
                    shortUserInfoDto.setOnline(userWsSessionService.findByUserId(shortUserInfoDto.getId()).size() > 0);
                }
            }
            return userProfileInfoDto;
        }
        return null;
    }

    @Override
    public MenuDataDto getMenuData() {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(user.getUsername());
        MenuDataDto menuDataDto = new MenuDataDto();
        menuDataDto.setNumOfMessages(0);
        List<UserChat> userChatList = userChatService.findByFirstUserIdOrSecondUserId(userDbi.getId());
        int numOfUnreadMessages = 0;
        for (UserChat userChat : userChatList) {
            numOfUnreadMessages += userChat.getFirstUserId().equals(userDbi.getId()) ? userChat.getFirstUserNumOfUnreadMessages() : userChat.getSecondUserNumOfUnreadMessages();
        }
        menuDataDto.setNumOfMessages(numOfUnreadMessages);
        menuDataDto.setNumOfRequestsToFriends(friendsService.findNumOfRequestsToFriends(userDbi.getId()));
        return menuDataDto;
    }

    @Override
    public SettingsDto getSettings() {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(user.getUsername());
        return Mapper.mapToSettingsDto(userDbi, imageStorageProperties);
    }

    @Override
    public FormStatusDto changeBasicSettings(MultipartFile image, String basicSettingsJson) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(user.getUsername());
        FormStatusDto formStatusDto = new FormStatusDto();
        try {
            Gson gson = new Gson();
            BasicSettingsDto basicSettingsDto = gson.fromJson(basicSettingsJson, BasicSettingsDto.class);
            formStatusDto = settingsValidator.validateBasicSettings(image, basicSettingsDto, userDbi.getUsername(), userDbi.getEmail());
            if (formStatusDto.getStatus().equals(StatusCode.SUCCESS)) {
                UserInfo userInfo = userDbi.getUserInfo();
                if (image != null) {
                    if (!userInfo.getAvatarName().equals(systemProperties.getDefaultUserAvatarName())) {
                        imageStorageService.deleteImage(userInfo.getAvatarName());
                    }
                    SaveImageResponseDto response = imageStorageService.saveImage(image);
                    userInfo.setAvatarName(response.getName());
                }
                userDbi.setFirstName(basicSettingsDto.getFirstName());
                userDbi.setLastName(basicSettingsDto.getLastName());
                userDbi.setUsername(basicSettingsDto.getUsername());

                userInfo.setDateOfBirth(Mapper.mapToTimestamp(basicSettingsDto.getDayOfBirth(), basicSettingsDto.getMonthOfBirth(), basicSettingsDto.getYearOfBirth()));
                userInfo.setSex(Sex.valueOf(basicSettingsDto.getSex()));
                userService.save(userDbi);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return formStatusDto;
    }

    @Override
    public FormStatusDto changeAdditionalSettings(AdditionalSettingsDto additionalSettingsDto) {
        FormStatusDto formStatusDto = settingsValidator.validateAdditionalSettings(additionalSettingsDto);
        if (formStatusDto.getStatus().equals(StatusCode.SUCCESS)) {
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userDbi = userService.findByUsername(user.getUsername());
            UserInfo userInfo = userDbi.getUserInfo();
            userInfo.setAboutYourself(additionalSettingsDto.getAboutYourself());
            userInfo.setCity(additionalSettingsDto.getCity());
            userInfo.setSchool(additionalSettingsDto.getSchool());
            userInfo.setUniversity(additionalSettingsDto.getUniversity());
            userService.save(userDbi);
        }
        return formStatusDto;
    }

    @Override
    public SearchResultDto searchUsers(String searchRequest) {
        SearchResultDto searchResultDto = new SearchResultDto();
        if (searchRequest.length() == 0) {
            return searchResultDto;
        }
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String[] searchParams = searchRequest.split(" ");
        List<ShortUserInfoDto> userDtoList = new ArrayList<>();
        if (searchParams.length == 1) {
            List<User> userList = userService.findUsersByText(searchParams[0] + "%", user.getUsername(), 20);
            userDtoList = Mapper.mapShortUserInfoDtoList(userList, imageStorageProperties);
        } else if (searchParams.length > 1) {
            List<User> userList = userService.findUsersByFirstNameAndLastName(searchParams[0] + "%", searchParams[1] + "%", user.getUsername(), 20);
            userDtoList = Mapper.mapShortUserInfoDtoList(userList, imageStorageProperties);
        }
        for (ShortUserInfoDto shortUserInfoDto: userDtoList) {
            shortUserInfoDto.setOnline(userWsSessionService.findByUserId(shortUserInfoDto.getId()).size() > 0);
        }
        searchResultDto.setUserList(userDtoList);
        return searchResultDto;
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
            UserPhoto userPhoto = userPhotoService.findById(photoId);
            if (userPhoto != null && userPhoto.getUserId().equals(userDbi.getId())) {
                String photoName = userPhoto.getName();
                imageStorageService.deleteImage(photoName);
                return userPhotoService.deleteByIdAndUserId(photoId, userDbi.getId());
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return 0;
    }

    @Override
    public boolean changePhotoLike(Long photoId) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(user.getUsername());
        UserPhoto userPhoto = userPhotoService.findById(photoId);
        if (userPhoto != null) {
            PhotoLike photoLike = photoLikeService.findByPhotoIdAndUserId(userPhoto.getId(), userDbi.getId());
            if (photoLike == null) {
                userPhoto.setNumOfLikes(userPhoto.getNumOfLikes() + 1);
                userPhotoService.save(userPhoto);
                photoLike = new PhotoLike();
                photoLike.setPhotoId(userPhoto.getId());
                photoLike.setUserId(userDbi.getId());
                photoLikeService.save(photoLike);
            } else {
                userPhoto.setNumOfLikes(userPhoto.getNumOfLikes() - 1);
                userPhotoService.save(userPhoto);
                photoLikeService.delete(photoLike);
            }
            return true;
        }
        return false;
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
            UserPost userPost = userPostService.findById(postId);
            if (userPost != null && userPost.getUserId().equals(userDbi.getId())) {
                String photoName = userPost.getPhotoName();
                imageStorageService.deleteImage(photoName);
                return userPostService.deleteByIdAndUserId(postId, userDbi.getId());
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return 0;
    }

    @Override
    public boolean changePostLike(Long postId) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(user.getUsername());
        UserPost userPost = userPostService.findById(postId);
        if (userPost != null) {
            PostLike postLike = postLikeService.findByPostIdAndUserId(userPost.getId(), userDbi.getId());
            if (postLike == null) {
                userPost.setNumOfLikes(userPost.getNumOfLikes() + 1);
                userPostService.save(userPost);

                postLike = new PostLike();
                postLike.setPostId(userPost.getId());
                postLike.setUserId(userDbi.getId());
                postLikeService.save(postLike);
            } else {
                userPost.setNumOfLikes(userPost.getNumOfLikes() - 1);
                userPostService.save(userPost);

                postLikeService.delete(postLike);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<UserPostDto> getUserPostList(String username, String beforeTimeStr) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(user.getUsername());
        User targetUser = userService.findByUsername(username);
        if (targetUser != null) {
            Timestamp beforeTime = null;
            if (beforeTimeStr != null) {
                try {
                    beforeTime = new Timestamp(Mapper.DATE_FORMAT.parse(beforeTimeStr).getTime());
                } catch (ParseException e) {
                    LOGGER.error(e);
                }
            }
            List<UserPost> userPostList = userPostService.findByUserIdAndCreationTimeBeforeOrderByCreationTimeDesc(targetUser.getId(), beforeTime, 10);
            List<Long> postsIdList = getPostsIdList(userPostList);
            List<PostLike> postLikeList = postLikeService.findByPostIdInAndUserId(postsIdList, userDbi.getId());
            return Mapper.mapToUserPostDtoList(userPostList, postLikeList, imageStorageProperties);
        }
        return null;
    }

    @Override
    public List<PostDto> getPostListBlock(String beforeTimeStr) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(user.getUsername());
        List<Friends> friendsList = friendsService.findFriends(userDbi.getId());
        List<Long> friendsIdList = getFriendsIdList(userDbi.getId(), friendsList);
        friendsIdList.add(userDbi.getId());
        List<User> userList = userService.findByIdIn(friendsIdList);
        Timestamp beforeTime = null;
        if (beforeTimeStr != null) {
            try {
                beforeTime = new Timestamp(Mapper.DATE_FORMAT.parse(beforeTimeStr).getTime());
            } catch (ParseException e) {
                LOGGER.error(e);
            }
        }
        List<UserPost> userPostList = userPostService.findByUserIdInAndCreationTimeBeforeOrderByCreationTimeDesc(friendsIdList, beforeTime, 10);
        List<Long> postsIdList = getPostsIdList(userPostList);
        List<PostLike> postLikeList = postLikeService.findByPostIdInAndUserId(postsIdList, userDbi.getId());
        List<PostDto> postDtoList = Mapper.mapToPostDtoList(userList, userPostList, postLikeList, imageStorageProperties);
        for (PostDto postDto: postDtoList) {
            if (postDto.getShortUserInfo().getId().equals(userDbi.getId())) {
                postDto.getShortUserInfo().setOnline(true);
            } else {
                postDto.getShortUserInfo().setOnline(userWsSessionService.findByUserId(postDto.getShortUserInfo().getId()).size() > 0);
            }
        }
        return postDtoList;
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

    @Override
    public ChatInfoDataDto getChatInfoData(String chatWith) {
        org.springframework.security.core.userdetails.User userRequester = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(userRequester.getUsername());
        if (chatWith != null) {
            createNewChatAndNotify(chatWith, userDbi);
        }
        List<UserChat> userChatList = userChatService.findByFirstUserIdOrSecondUserId(userDbi.getId());
        List<Long> chatMemberIdList = getChatMemberIdList(userDbi.getId(), userChatList);
        List<User> chatMemberList = userService.findByIdIn(chatMemberIdList);
        ChatInfoDataDto chatInfoDataDto = Mapper.mapToChatInfoDataDto(userDbi.getId(), userChatList, chatMemberList, imageStorageProperties);
        for (UserChatInfoDto userChatInfoDto : chatInfoDataDto.getUserChatInfoList()) {
            userChatInfoDto.setOnline(userWsSessionService.findByUserId(userChatInfoDto.getUserId()).size() > 0);
        }
        return chatInfoDataDto;
    }

    @Override
    public List<ChatMessageDto> getChatMessageListBlock(Long chatId, String beforeTimeStr) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(user.getUsername());
        UserChat userChat = userChatService.findById(chatId);
        if (userChat != null && isChatMember(userChat, userDbi.getId())) {
            int count = 50;
            Timestamp beforeTime = null;
            if (beforeTimeStr != null) {
                try {
                    beforeTime = new Timestamp(Mapper.DATE_FORMAT.parse(beforeTimeStr).getTime());
                } catch (ParseException e) {
                    LOGGER.error(e);
                }
            } else {
                int numOfUnreadMessages;
                if (userChat.getFirstUserId().equals(userDbi.getId())) {
                    numOfUnreadMessages = Math.toIntExact(userChat.getFirstUserNumOfUnreadMessages());
                } else {
                    numOfUnreadMessages = Math.toIntExact(userChat.getSecondUserNumOfUnreadMessages());
                }
                count = Math.max(numOfUnreadMessages, count);
            }
            List<UserChatMessage> userChatMessageList = userChatMessageService.findByChatIdAndCreationTimeBeforeOrderByCreationTimeDesc(chatId, beforeTime, count);
            List<Long> chatMemberIdList = getChatMemberIdList(userDbi.getId(), userChat);
            List<User> chatMemberList = userService.findByIdIn(chatMemberIdList);
            Map<Long, User> chatMemberMap = new HashMap<>();
            for (User chatMember : chatMemberList) {
                chatMemberMap.put(chatMember.getId(), chatMember);
            }
            Collections.reverse(userChatMessageList);
            return Mapper.mapToChatMessageDtoList(chatMemberMap, userChatMessageList, imageStorageProperties);
        }
        return null;
    }

    @Override
    public WebSocketSessionKeyDto getWebSocketSessionKey() {
        org.springframework.security.core.userdetails.User userRequester = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(userRequester.getUsername());
        return new WebSocketSessionKeyDto(webSocketSessionsStorage.generateWebSocketRequestKey(userDbi.getId()));
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

    private void createNewChatAndNotify(String chatWith, User ownerUser) {
        User targetUser = userService.findByUsername(chatWith);
        if (targetUser != null) {
            List<UserChat> userChatList = userChatService.findByChatMembers(ownerUser.getId(), targetUser.getId());
            if (userChatList.size() == 0) {
                UserChat userChat = new UserChat();
                userChat.setFirstUserId(ownerUser.getId());
                userChat.setSecondUserId(targetUser.getId());
                userChatService.save(userChat);
                UserChatInfoDto userChatInfoDto = Mapper.mapToUserChatInfoDto(targetUser.getId(), userChat, ownerUser, imageStorageProperties);
                chatMessagesHandler.sendInfoAboutNewChat(targetUser.getId(), userChatInfoDto);
            }
        }
    }

    private List<Long> getUsersIdList(List<UserChatMessage> userChatMessageList) {
        List<Long> usersIdList = new ArrayList<>();
        for (UserChatMessage userChatMessage : userChatMessageList) {
            usersIdList.add(userChatMessage.getUserId());
        }
        return usersIdList;
    }

    private List<Long> getFriendsIdList(Long userId, List<Friends> friendsList) {
        List<Long> friendsIdList = new ArrayList<>();
        for (Friends friends : friendsList) {
            friendsIdList.add(Objects.equals(friends.getFirstUserId(), userId) ? friends.getSecondUserId() : friends.getFirstUserId());
        }
        return friendsIdList;
    }

    private List<Long> getChatMemberIdList(Long userId, List<UserChat> userChatList) {
        List<Long> chatMemberIdList = new ArrayList<>();
        for (UserChat userChat : userChatList) {
            chatMemberIdList.add(getChatMemberId(userId, userChat));
        }
        chatMemberIdList.add(userId);
        return chatMemberIdList;
    }

    private List<Long> getChatMemberIdList(Long userId, UserChat userChat) {
        List<Long> chatMemberIdList = new ArrayList<>();
        chatMemberIdList.add(getChatMemberId(userId, userChat));
        chatMemberIdList.add(userId);
        return chatMemberIdList;
    }

    private Long getChatMemberId(Long userId, UserChat userChat) {
        return Objects.equals(userChat.getFirstUserId(), userId) ? userChat.getSecondUserId() : userChat.getFirstUserId();
    }

    private List<Long> getUserChatIdList(List<UserChat> userChatList) {
        List<Long> userChatIdList = new ArrayList<>();
        for (UserChat userChat : userChatList) {
            userChatIdList.add(userChat.getId());
        }
        return userChatIdList;
    }

    private List<Long> getFriendRequestsIdList(List<Friends> friendsList) {
        List<Long> friendsIdList = new ArrayList<>();
        for (Friends friends : friendsList) {
            friendsIdList.add(friends.getFirstUserId());
        }
        return friendsIdList;
    }

    private List<Long> getPhotosIdList(List<UserPhoto> userPhotoList) {
        List<Long> photoIdList = new ArrayList<>();
        for (UserPhoto userPhoto : userPhotoList) {
            photoIdList.add(userPhoto.getId());
        }
        return photoIdList;
    }

    private List<Long> getPostsIdList(List<UserPost> userPostList) {
        List<Long> postIdList = new ArrayList<>();
        for (UserPost userPost : userPostList) {
            postIdList.add(userPost.getId());
        }
        return postIdList;
    }

    private boolean isChatMember(UserChat userChat, Long userId) {
        return userChat.getFirstUserId().equals(userId) || userChat.getSecondUserId().equals(userId);
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
    public void setPhotoLikeService(PhotoLikeService photoLikeService) {
        this.photoLikeService = photoLikeService;
    }

    @Autowired
    public void setPostLikeService(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
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
    public void setImageStorageService(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @Autowired
    public void setUserChatMessageService(UserChatMessageService userChatMessageService) {
        this.userChatMessageService = userChatMessageService;
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
    public void setResetPasswordValidator(ResetPasswordValidator resetPasswordValidator) {
        this.resetPasswordValidator = resetPasswordValidator;
    }

    @Autowired
    public void setChatMessagesHandler(ChatMessagesHandler chatMessagesHandler) {
        this.chatMessagesHandler = chatMessagesHandler;
    }

    @Autowired
    public void setSettingsValidator(SettingsValidator settingsValidator) {
        this.settingsValidator = settingsValidator;
    }

    @Autowired
    public void setRegistrationRequestService(RegistrationRequestService registrationRequestService) {
        this.registrationRequestService = registrationRequestService;
    }

    @Autowired
    public void setRegistrationService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Autowired
    public void setRestoreRequestService(RestoreRequestService restoreRequestService) {
        this.restoreRequestService = restoreRequestService;
    }

    @Autowired
    public void setUserWsSessionService(UserWsSessionService userWsSessionService) {
        this.userWsSessionService = userWsSessionService;
    }

    @Autowired
    public void setWebSocketSessionsStorage(WebSocketSessionsStorage webSocketSessionsStorage) {
        this.webSocketSessionsStorage = webSocketSessionsStorage;
    }

    @Autowired
    public void setRestoreService(RestoreService restoreService) {
        this.restoreService = restoreService;
    }

    @Autowired
    public void setUserChatService(UserChatService userChatService) {
        this.userChatService = userChatService;
    }
}
