package andrew.project.socialNetwork.backend.api.mappers;

import andrew.project.socialNetwork.backend.api.constants.Sex;
import andrew.project.socialNetwork.backend.api.data.NewChatMessage;
import andrew.project.socialNetwork.backend.api.dtos.*;
import andrew.project.socialNetwork.backend.api.entities.*;
import andrew.project.socialNetwork.backend.api.properties.ImageStorageProperties;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Mapper {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static UserProfileInfoDto mapToUserProfileInfoDto(User user, List<UserPhoto> userPhotoList, List<PhotoLike> photoLikeList, List<User> userFriendList, int numOfPosts, Friends friends, ImageStorageProperties properties) {
        UserProfileInfoDto userProfileInfoDto = new UserProfileInfoDto();
        UserInfo userInfo = user.getUserInfo();
        userProfileInfoDto.setId(user.getId());
        userProfileInfoDto.setUsername(user.getUsername());
        userProfileInfoDto.setFirstName(user.getFirstName());
        userProfileInfoDto.setLastName(user.getLastName());
        userProfileInfoDto.setAvatarUri(mapToImageUri(userInfo.getAvatarName(), properties));
        userProfileInfoDto.setDateOfBirth(userInfo.getDateOfBirth());
        userProfileInfoDto.setSex(userInfo.getSex());
        userProfileInfoDto.setCity(userInfo.getCity());
        userProfileInfoDto.setSchool(userInfo.getSchool());
        userProfileInfoDto.setUniversity(userInfo.getUniversity());
        userProfileInfoDto.setAboutYourself(userInfo.getAboutYourself());
        userProfileInfoDto.setNumOfPosts(numOfPosts);
        if (friends != null) {
            userProfileInfoDto.setFriend(friends.getAccepted());
            userProfileInfoDto.setRequestToFriends(Objects.equals(user.getId(), friends.getSecondUserId()));
        }
        if (!CollectionUtils.isEmpty(userPhotoList)) {
            userProfileInfoDto.setUserPhotoList(mapToUserPhotoDtoList(userPhotoList, photoLikeList, properties));
        }
        if (!CollectionUtils.isEmpty(userFriendList)) {
            userProfileInfoDto.setUserFriendList(mapShortUserInfoDtoList(userFriendList, properties));
        }
        return userProfileInfoDto;
    }

    public static UserFriendsInfoDto mapToUserFriendsInfoDto(User user, List<User> userFriendList, List<User> userFriendRequestList, ImageStorageProperties properties) {
        UserFriendsInfoDto userFriendsInfoDto = new UserFriendsInfoDto();
        userFriendsInfoDto.setShortUserInfo(mapShortUserInfoDto(user, properties));
        if (!CollectionUtils.isEmpty(userFriendList)) {
            userFriendsInfoDto.setUserFriendList(mapShortUserInfoDtoList(userFriendList, properties));
        }
        if (!CollectionUtils.isEmpty(userFriendRequestList)) {
            userFriendsInfoDto.setUserFriendRequestList(mapShortUserInfoDtoList(userFriendRequestList, properties));
        }
        return userFriendsInfoDto;
    }

    private static ShortUserInfoDto mapShortUserInfoDto(User user, ImageStorageProperties properties) {
        ShortUserInfoDto shortUserInfoDto = new ShortUserInfoDto();
        UserInfo userInfo = user.getUserInfo();
        shortUserInfoDto.setId(user.getId());
        shortUserInfoDto.setUsername(user.getUsername());
        shortUserInfoDto.setFirstName(user.getFirstName());
        shortUserInfoDto.setLastName(user.getLastName());
        shortUserInfoDto.setAvatarUri(mapToImageUri(userInfo.getAvatarName(), properties));
        return shortUserInfoDto;
    }

    public static List<PostDto> mapToPostDtoList(List<User> userList, List<UserPost> userPostList, List<PostLike> postLikeList, ImageStorageProperties properties) {
        List<PostDto> postDtoList = new ArrayList<>();
        Map<Long, User> userMap = new HashMap<>();
        for (User user : userList) {
            userMap.put(user.getId(), user);
        }
        Map<Long, PostLike> postLikeMap = new HashMap<>();
        for (PostLike postLike : postLikeList) {
            postLikeMap.put(postLike.getPostId(), postLike);
        }
        for (UserPost userPost : userPostList) {
            User user = userMap.get(userPost.getUserId());
            PostDto postDto = mapToPostDto(userPost, user, properties);
            PostLike postLike = postLikeMap.get(userPost.getId());
            if (postLike != null) {
                postDto.setLike(true);
            }
            postDtoList.add(postDto);
        }
        return postDtoList;
    }

    private static PostDto mapToPostDto(UserPost userPost, User user, ImageStorageProperties properties) {
        PostDto postDto = new PostDto();
        postDto.setId(userPost.getId());
        postDto.setPhotoUri(mapToImageUri(userPost.getPhotoName(), properties));
        postDto.setText(userPost.getText());
        postDto.setNumOfLikes(userPost.getNumOfLikes());
        postDto.setCreationTime(DATE_FORMAT.format(userPost.getCreationTime()));
        if (user != null) {
            UserInfo userInfo = user.getUserInfo();
            ShortUserInfoDto shortUserInfoDto = new ShortUserInfoDto();
            shortUserInfoDto.setId(user.getId());
            shortUserInfoDto.setUsername(user.getUsername());
            shortUserInfoDto.setFirstName(user.getFirstName());
            shortUserInfoDto.setLastName(user.getLastName());
            shortUserInfoDto.setAvatarUri(mapToImageUri(userInfo.getAvatarName(), properties));
            postDto.setShortUserInfo(shortUserInfoDto);
        }
        return postDto;
    }

    public static List<ShortUserInfoDto> mapShortUserInfoDtoList(List<User> userList, ImageStorageProperties properties) {
        List<ShortUserInfoDto> shortUserInfoDtoList = new ArrayList<>();
        for (User user : userList) {
            shortUserInfoDtoList.add(mapShortUserInfoDto(user, properties));
        }
        return shortUserInfoDtoList;
    }

    public static List<UserPhotoDto> mapToUserPhotoDtoList(List<UserPhoto> userPhotoList, List<PhotoLike> photoLikeList, ImageStorageProperties properties) {
        Map<Long, UserPhotoDto> userPhotoDtoMap = new HashMap<>();
        for (UserPhoto userPhoto : userPhotoList) {
            userPhotoDtoMap.put(userPhoto.getId(), mapToPhotoDto(userPhoto, properties));
        }
        for (PhotoLike photoLike : photoLikeList) {
            UserPhotoDto userPhotoDto = userPhotoDtoMap.get(photoLike.getPhotoId());
            if (userPhotoDto != null) {
                userPhotoDto.setLike(true);
            }
        }
        return new ArrayList<>(userPhotoDtoMap.values());
    }

    public static UserPhotoDto mapToPhotoDto(UserPhoto userPhoto, ImageStorageProperties properties) {
        UserPhotoDto userPhotoDto = new UserPhotoDto();
        userPhotoDto.setId(userPhoto.getId());
        userPhotoDto.setPhotoUri(mapToImageUri(userPhoto.getName(), properties));
        userPhotoDto.setNumOfLikes(userPhoto.getNumOfLikes());
        userPhotoDto.setLoadTime(DATE_FORMAT.format(userPhoto.getLoadTime()));
        return userPhotoDto;
    }

    public static List<UserPostDto> mapToUserPostDtoList(List<UserPost> userPostList, List<PostLike> postLikeList, ImageStorageProperties properties) {
        List<UserPostDto> userPostDtoList = new ArrayList<>();
        Map<Long, PostLike> postLikeMap = new HashMap<>();
        for (PostLike postLike : postLikeList) {
            postLikeMap.put(postLike.getPostId(), postLike);
        }
        for (UserPost userPost : userPostList) {
            UserPostDto userPostDto = mapToUserPostDto(userPost, properties);
            PostLike postLike = postLikeMap.get(userPost.getId());
            if (postLike != null) {
                userPostDto.setLike(true);
            }
            userPostDtoList.add(userPostDto);
        }
        return userPostDtoList;
    }

    public static UserPostDto mapToUserPostDto(UserPost userPost, ImageStorageProperties properties) {
        UserPostDto userPostDto = new UserPostDto();
        userPostDto.setId(userPost.getId());
        userPostDto.setPhotoUri(mapToImageUri(userPost.getPhotoName(), properties));
        userPostDto.setText(userPost.getText());
        userPostDto.setNumOfLikes(userPost.getNumOfLikes());
        userPostDto.setCreationTime(DATE_FORMAT.format(userPost.getCreationTime()));
        return userPostDto;
    }

    public static SaveImageRequestDto mapToSaveImageResponseDto(MultipartFile file) throws Exception {
        SaveImageRequestDto requestDto = new SaveImageRequestDto();
        requestDto.setBytes(file.getBytes());
        requestDto.setType(FilenameUtils.getExtension(file.getResource().getFilename()));
        return requestDto;
    }

    public static DeleteImageRequestDto mapToDeleteImageRequestDto(String imageName) {
        DeleteImageRequestDto requestDto = new DeleteImageRequestDto();
        requestDto.setName(imageName);
        return requestDto;
    }

    public static String mapToImageUri(String imageName, ImageStorageProperties imageStorageProperties) {
        if (imageName == null) {
            return null;
        }
        return UriComponentsBuilder
                .fromUriString(imageStorageProperties.getUrl())
                .path(imageStorageProperties.getGetImageEndpoint())
                .queryParam("name", imageName).build().toString();
    }

    public static RegistrationRequest mapRegistrationRequest(RegFormDto regFormDto) throws ParseException {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setFirstName(regFormDto.getFirstName());
        registrationRequest.setLastName(regFormDto.getLastName());
        registrationRequest.setEmail(regFormDto.getEmail());
        registrationRequest.setUsername(regFormDto.getUsername());
        registrationRequest.setPassword(regFormDto.getPassword());
        registrationRequest.setDateOfBirth(mapToTimestamp(regFormDto.getDayOfBirth(), regFormDto.getMonthOfBirth(), regFormDto.getYearOfBirth()));
        registrationRequest.setSex(Sex.valueOf(regFormDto.getSex()));
        return registrationRequest;
    }

    public static User mapToUser(RegistrationRequest registrationRequest) {
        User user = new User();
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(registrationRequest.getPassword());

        UserInfo userInfo = new UserInfo();
        userInfo.setDateOfBirth(registrationRequest.getDateOfBirth());
        userInfo.setSex(registrationRequest.getSex());

        user.setUserInfo(userInfo);
        return user;
    }

    public static SettingsDto mapToSettingsDto(User user, ImageStorageProperties properties) {
        UserInfo userInfo = user.getUserInfo();

        SettingsDto settingsDto = new SettingsDto();

        BasicSettingsDto basicSettingsDto = new BasicSettingsDto();
        basicSettingsDto.setAvatarUri(mapToImageUri(userInfo.getAvatarName(), properties));
        basicSettingsDto.setFirstName(user.getFirstName());
        basicSettingsDto.setLastName(user.getLastName());
        basicSettingsDto.setUsername(user.getUsername());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(userInfo.getDateOfBirth().getTime());
        basicSettingsDto.setDayOfBirth(calendar.get(Calendar.DAY_OF_MONTH));
        basicSettingsDto.setMonthOfBirth(calendar.get(Calendar.MONTH) + 1);
        basicSettingsDto.setYearOfBirth(calendar.get(Calendar.YEAR));
        basicSettingsDto.setSex(userInfo.getSex().name());

        AdditionalSettingsDto additionalSettingsDto = new AdditionalSettingsDto();
        additionalSettingsDto.setAboutYourself(userInfo.getAboutYourself());
        additionalSettingsDto.setCity(userInfo.getCity());
        additionalSettingsDto.setSchool(userInfo.getSchool());
        additionalSettingsDto.setUniversity(userInfo.getUniversity());

        settingsDto.setBasicSettings(basicSettingsDto);
        settingsDto.setAdditionalSettings(additionalSettingsDto);

        return settingsDto;
    }

    public static Timestamp mapToTimestamp(int day, int month, int year) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return new Timestamp(simpleDateFormat.parse(String.format("%d.%d.%d", day, month, year)).getTime());
    }

    public static UserChatMessage mapToUserChatMessage(UserChat userChat, User owner, NewChatMessage newChatMessage) {
        UserChatMessage userChatMessage = new UserChatMessage();
        userChatMessage.setChatId(userChat.getId());
        userChatMessage.setUserId(owner.getId());
        userChatMessage.setText(newChatMessage.getText());
        return userChatMessage;
    }

    public static ChatInfoDataDto mapToChatInfoDataDto(Long targetUserId, List<UserChat> userChatList, List<User> chatMemberList, List<List<UserChatMessage>> userChatMessageListList, ImageStorageProperties properties) {
        ChatInfoDataDto chatInfoDataDto = new ChatInfoDataDto();
        chatInfoDataDto.setUserId(targetUserId);
        chatInfoDataDto.setUserChatInfoList(mapToUserChatInfoDtoList(targetUserId, userChatList, chatMemberList, userChatMessageListList, properties));
        return chatInfoDataDto;
    }

    public static List<UserChatInfoDto> mapToUserChatInfoDtoList(Long targetUserId, List<UserChat> userChatList, List<User> chatMemberList, List<List<UserChatMessage>> userChatMessageListList, ImageStorageProperties properties) {
        List<UserChatInfoDto> userChatInfoDtoList = new ArrayList<>();

        Map<Long, User> chatMemberMap = new HashMap<>();
        for (User chatMember : chatMemberList) {
            chatMemberMap.put(chatMember.getId(), chatMember);
        }
        for (int i = 0; i < userChatList.size(); i++) {
            UserChat userChat = userChatList.get(i);
            List<UserChatMessage> userChatMessageList = userChatMessageListList.get(i);
            userChatInfoDtoList.add(mapToUserChatInfoDto(targetUserId, userChat, chatMemberMap, userChatMessageList, properties));
        }
        return userChatInfoDtoList;
    }

    public static UserChatInfoDto mapToUserChatInfoDto(Long targetUserId, UserChat userChat, Map<Long, User> chatMemberMap, List<UserChatMessage> userChatMessageList, ImageStorageProperties properties) {
        UserChatInfoDto userChatInfoDto = new UserChatInfoDto();
        userChatInfoDto.setId(userChat.getId());
        if (userChat.getFirstUserId().equals(targetUserId)) {
            userChatInfoDto.setNumOfUnreadMessages(userChat.getFirstUserNumOfUnreadMessages());
        } else {
            userChatInfoDto.setNumOfUnreadMessages(userChat.getSecondUserNumOfUnreadMessages());
        }
        Long memberId = userChat.getFirstUserId().equals(targetUserId) ? userChat.getSecondUserId() : userChat.getFirstUserId();
        User chatMember = chatMemberMap.get(memberId);
        userChatInfoDto.setUserId(chatMember.getId());
        userChatInfoDto.setUsername(chatMember.getUsername());
        userChatInfoDto.setFirstName(chatMember.getFirstName());
        userChatInfoDto.setLastName(chatMember.getLastName());
        UserInfo chatMemberInfo = chatMember.getUserInfo();
        userChatInfoDto.setAvatarUri(mapToImageUri(chatMemberInfo.getAvatarName(), properties));
        userChatInfoDto.setChatMessageList(mapToChatMessageDtoList(chatMemberMap, userChatMessageList, properties));
        return userChatInfoDto;
    }

    public static List<ChatMessageDto> mapToChatMessageDtoList(Map<Long, User> chatMemberMap, List<UserChatMessage> userChatMessageList, ImageStorageProperties properties) {
        List<ChatMessageDto> chatMessageDtoList = new ArrayList<>();
        for (UserChatMessage userChatMessage : userChatMessageList) {
            User messageOwner = chatMemberMap.get(userChatMessage.getUserId());
            chatMessageDtoList.add(mapToChatMessageDto(userChatMessage, messageOwner, properties));
        }
        return chatMessageDtoList;
    }

    public static ChatMessageDto mapToChatMessageDto(UserChatMessage userChatMessage, User messageOwner, ImageStorageProperties properties) {
        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setId(userChatMessage.getId());
        chatMessageDto.setChatId(userChatMessage.getChatId());
        chatMessageDto.setUserId(messageOwner.getId());
        chatMessageDto.setUsername(messageOwner.getUsername());
        chatMessageDto.setFirstName(messageOwner.getFirstName());
        chatMessageDto.setLastName(messageOwner.getLastName());
        UserInfo ownerInfo = messageOwner.getUserInfo();
        chatMessageDto.setAvatarUri(mapToImageUri(ownerInfo.getAvatarName(), properties));
        chatMessageDto.setCreationTime(DATE_FORMAT.format(userChatMessage.getCreationTime()));
        chatMessageDto.setText(userChatMessage.getText());
        chatMessageDto.setRevised(userChatMessage.getRevised());
        return chatMessageDto;
    }

}
