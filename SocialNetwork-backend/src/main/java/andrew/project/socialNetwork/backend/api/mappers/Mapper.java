package andrew.project.socialNetwork.backend.api.mappers;

import andrew.project.socialNetwork.backend.api.constants.Sex;
import andrew.project.socialNetwork.backend.api.dtos.*;
import andrew.project.socialNetwork.backend.api.entities.*;
import andrew.project.socialNetwork.backend.api.properties.ImageStorageProperties;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Mapper {

    public static UserProfileInfoDto mapToUserProfileInfoDto(User user, List<UserPhoto> userPhotoList, List<UserPost> userPostList, List<User> userFriendList, Friends friends, ImageStorageProperties properties) {
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
        if (friends != null) {
            userProfileInfoDto.setFriend(friends.getAccepted());
            userProfileInfoDto.setRequestToFriends(Objects.equals(user.getId(), friends.getSecondUserId()));
        }
        if (!CollectionUtils.isEmpty(userPhotoList)) {
            userProfileInfoDto.setUserPhotoList(mapToPhotoDtoList(userPhotoList, properties));
        }
        if (!CollectionUtils.isEmpty(userPostList)) {
            userProfileInfoDto.setUserPostList(mapToPostDtoList(userPostList, properties));
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

    public static NewsDto mapToNewsDto(List<User> userList, List<UserPost> userPostList, ImageStorageProperties properties) {
        NewsDto newsDto = new NewsDto();
        if (!CollectionUtils.isEmpty(userPostList)) {
            newsDto.setPostList(mapToPostDtoList(userList, userPostList, properties));
        }
        return newsDto;
    }

    private static List<PostDto> mapToPostDtoList(List<User> userList, List<UserPost> userPostList, ImageStorageProperties properties) {
        List<PostDto> postList = new ArrayList<>();
        Map<Long, User> userMap = new HashMap<>();
        for (User user : userList) {
            userMap.put(user.getId(), user);
        }
        for (UserPost userPost : userPostList) {
            User user = userMap.get(userPost.getUserId());
            postList.add(mapToPostDto(userPost, user, properties));
        }
        return postList;
    }

    private static PostDto mapToPostDto(UserPost userPost, User user, ImageStorageProperties properties) {
        PostDto postDto = new PostDto();
        postDto.setId(userPost.getId());
        postDto.setPhotoUri(mapToImageUri(userPost.getPhotoName(), properties));
        postDto.setText(userPost.getText());
        postDto.setNumOfLikes(userPost.getNumOfLikes());
        postDto.setCreationTime(userPost.getCreationTime());
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

    public static List<UserPhotoDto> mapToPhotoDtoList(List<UserPhoto> userPhotoList, ImageStorageProperties properties) {
        List<UserPhotoDto> userPhotoDtoList = new ArrayList<>();
        for (UserPhoto userPhoto : userPhotoList) {
            userPhotoDtoList.add(mapToPhotoDto(userPhoto, properties));
        }
        return userPhotoDtoList;
    }

    public static UserPhotoDto mapToPhotoDto(UserPhoto userPhoto, ImageStorageProperties properties) {
        UserPhotoDto userPhotoDto = new UserPhotoDto();
        userPhotoDto.setId(userPhoto.getId());
        userPhotoDto.setPhotoUri(mapToImageUri(userPhoto.getName(), properties));
        userPhotoDto.setNumOfLikes(userPhoto.getNumOfLikes());
        userPhotoDto.setLoadTime(userPhoto.getLoadTime());
        return userPhotoDto;
    }

    public static List<UserPostDto> mapToPostDtoList(List<UserPost> userPostList, ImageStorageProperties properties) {
        List<UserPostDto> userPostDtoList = new ArrayList<>();
        for (UserPost userPost : userPostList) {
            userPostDtoList.add(mapToUserPostDto(userPost, properties));
        }
        return userPostDtoList;
    }

    public static UserPostDto mapToUserPostDto(UserPost userPost, ImageStorageProperties properties) {
        UserPostDto userPostDto = new UserPostDto();
        userPostDto.setId(userPost.getId());
        userPostDto.setPhotoUri(mapToImageUri(userPost.getPhotoName(), properties));
        userPostDto.setText(userPost.getText());
        userPostDto.setNumOfLikes(userPost.getNumOfLikes());
        userPostDto.setCreationTime(userPost.getCreationTime());
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setFirstName(regFormDto.getFirstName());
        registrationRequest.setLastName(regFormDto.getLastName());
        registrationRequest.setEmail(regFormDto.getEmail());
        registrationRequest.setUsername(regFormDto.getUsername());
        registrationRequest.setPassword(regFormDto.getPassword());
        registrationRequest.setDateOfBirth(new Timestamp(simpleDateFormat.parse(String.format("%d.%d.%d", regFormDto.getDayOfBirth(), regFormDto.getMonthOfBirth(), regFormDto.getYearOfBirth())).getTime()));
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
}
