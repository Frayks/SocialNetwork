package andrew.project.socialNetwork.backend.libraries;

import andrew.project.socialNetwork.backend.api.constants.AddToFriendsStatusCode;
import andrew.project.socialNetwork.backend.api.constants.Sex;
import andrew.project.socialNetwork.backend.api.constants.StatusCode;
import andrew.project.socialNetwork.backend.api.dtos.*;
import andrew.project.socialNetwork.backend.api.entities.*;
import andrew.project.socialNetwork.backend.api.handlers.ChatMessagesHandler;
import andrew.project.socialNetwork.backend.api.libraries.MainLib;
import andrew.project.socialNetwork.backend.api.mappers.Mapper;
import andrew.project.socialNetwork.backend.api.properties.ImageStorageProperties;
import andrew.project.socialNetwork.backend.api.properties.SystemProperties;
import andrew.project.socialNetwork.backend.api.services.*;
import andrew.project.socialNetwork.backend.api.storages.WebSocketSessionsStorage;
import andrew.project.socialNetwork.backend.api.utils.CommonUtil;
import andrew.project.socialNetwork.backend.api.validators.*;
import andrew.project.socialNetwork.backend.security.JwtProvider;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

@Component
public class MainLibImpl implements MainLib {

    private static final Logger LOGGER = LogManager.getLogger(MainLibImpl.class);

    private UserService userService;
    private FriendsService friendsService;
    private UserPostService userPostService;
    private PostLikeService postLikeService;
    private UserChatService userChatService;
    private UserPhotoService userPhotoService;
    private PhotoLikeService photoLikeService;
    private ImageStorageService imageStorageService;
    private UserWsSessionService userWsSessionService;
    private UserChatMessageService userChatMessageService;

    private PostValidator postValidator;
    private ImageValidator imageValidator;
    private SettingsValidator settingsValidator;

    private SystemProperties systemProperties;
    private ImageStorageProperties imageStorageProperties;
    private ChatMessagesHandler chatMessagesHandler;


    @Override
    public UserProfileInfoDto getUserProfileInfo(String username) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(user.getUsername());
        try {
            User targetUser = userService.findByUsername(username);
            if (targetUser != null) {
                int numOfPosts = userPostService.countByUserId(targetUser.getId());
                List<UserPhoto> userPhotoList = userPhotoService.findByUserIdOrderByLoadTimeDesc(targetUser.getId());
                List<Long> photoIdList = CommonUtil.getPhotoIdList(userPhotoList);
                List<PhotoLike> photoLikeList = photoLikeService.findByPhotoIdInAndUserId(photoIdList, userDbi.getId());
                org.springframework.security.core.userdetails.User userRequester = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User userRequesterDbi = userService.findByUsername(userRequester.getUsername());
                List<Friends> friendsList = friendsService.findFriends(targetUser.getId(), 9);
                List<Long> friendIdList = CommonUtil.getFriendIdList(targetUser.getId(), friendsList);
                List<User> userFriendList = userService.findByIdIn(friendIdList);
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
            List<Long> friendsIdList = CommonUtil.getFriendIdList(user.getId(), friendsList);
            List<User> userFriendList = userService.findByIdIn(friendsIdList);
            List<User> userFriendRequestList = null;
            org.springframework.security.core.userdetails.User userdetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (username.equals(userdetails.getUsername())) {
                List<Friends> friendRequestList = friendsService.findRequestsToFriends(user.getId());
                List<Long> friendRequestIdList = CommonUtil.getFriendRequestIdList(friendRequestList);
                userFriendRequestList = userService.findByIdIn(friendRequestIdList);
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
        for (ShortUserInfoDto shortUserInfoDto : userDtoList) {
            shortUserInfoDto.setOnline(userWsSessionService.findByUserId(shortUserInfoDto.getId()).size() > 0);
        }
        searchResultDto.setUserList(userDtoList);
        return searchResultDto;
    }

    @Override
    public UserPhotoDto addPhoto(MultipartFile file) {
        try {
            FormStatusDto formStatusDto = imageValidator.validate(file);
            if (formStatusDto.getStatus().equals(StatusCode.SUCCESS)) {
                SaveImageResponseDto response = imageStorageService.saveImage(file);
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User userDbi = userService.findByUsername(user.getUsername());
                UserPhoto userPhoto = new UserPhoto();
                userPhoto.setUserId(userDbi.getId());
                userPhoto.setLoadTime(new Timestamp(System.currentTimeMillis()));
                userPhoto.setName(response.getName());
                userPhoto = userPhotoService.save(userPhoto);
                return Mapper.mapToPhotoDto(userPhoto, imageStorageProperties);
            }
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
    public FormStatusDto createPost(MultipartFile image, String text) {
        FormStatusDto formStatusDto = new FormStatusDto();
        try {
            formStatusDto = postValidator.validate(image, text);
            if (formStatusDto.getStatus().equals(StatusCode.SUCCESS)) {
                SaveImageResponseDto response = null;
                if (image != null) {
                    response = imageStorageService.saveImage(image);
                }
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User userDbi = userService.findByUsername(user.getUsername());
                UserPost userPost = new UserPost();
                userPost.setUserId(userDbi.getId());
                userPost.setCreationTime(new Timestamp(System.currentTimeMillis()));
                userPost.setText(text);
                if (response != null) {
                    userPost.setPhotoName(response.getName());
                }
                userPostService.save(userPost);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return formStatusDto;
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
            List<Long> postIdList = CommonUtil.getPostIdList(userPostList);
            List<PostLike> postLikeList = postLikeService.findByPostIdInAndUserId(postIdList, userDbi.getId());
            return Mapper.mapToUserPostDtoList(userPostList, postLikeList, imageStorageProperties);
        }
        return null;
    }

    @Override
    public List<PostDto> getPostListBlock(String beforeTimeStr) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(user.getUsername());
        List<Friends> friendsList = friendsService.findFriends(userDbi.getId());
        List<Long> friendsIdList = CommonUtil.getFriendIdList(userDbi.getId(), friendsList);
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
        List<Long> postsIdList = CommonUtil.getPostIdList(userPostList);
        List<PostLike> postLikeList = postLikeService.findByPostIdInAndUserId(postsIdList, userDbi.getId());
        List<PostDto> postDtoList = Mapper.mapToPostDtoList(userList, userPostList, postLikeList, imageStorageProperties);
        for (PostDto postDto : postDtoList) {
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
        List<Long> chatMemberIdList = CommonUtil.getChatMemberIdList(userDbi.getId(), userChatList);
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
        if (userChat != null && CommonUtil.isChatMember(userChat, userDbi.getId())) {
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
            List<Long> chatMemberIdList = CommonUtil.getChatMemberIdList(userDbi.getId(), userChat);
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
    public void setChatMessagesHandler(ChatMessagesHandler chatMessagesHandler) {
        this.chatMessagesHandler = chatMessagesHandler;
    }

    @Autowired
    public void setSettingsValidator(SettingsValidator settingsValidator) {
        this.settingsValidator = settingsValidator;
    }

    @Autowired
    public void setImageValidator(ImageValidator imageValidator) {
        this.imageValidator = imageValidator;
    }

    @Autowired
    public void setPostValidator(PostValidator postValidator) {
        this.postValidator = postValidator;
    }

    @Autowired
    public void setUserWsSessionService(UserWsSessionService userWsSessionService) {
        this.userWsSessionService = userWsSessionService;
    }
    
    @Autowired
    public void setUserChatService(UserChatService userChatService) {
        this.userChatService = userChatService;
    }

}
