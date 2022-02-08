package andrew.project.socialNetwork.backend.api.mappers;

import andrew.project.socialNetwork.backend.api.dtos.UserFriendDto;
import andrew.project.socialNetwork.backend.api.dtos.UserPhotoDto;
import andrew.project.socialNetwork.backend.api.dtos.UserPostDto;
import andrew.project.socialNetwork.backend.api.dtos.UserProfileInfoDto;
import andrew.project.socialNetwork.backend.api.entities.*;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class UserMapper {

    public static UserProfileInfoDto mapToUserProfileInfoDto(User user, List<UserPhoto> userPhotoList, List<UserPost> userPostList, List<User> userFriendList, Friends friends) {
        UserProfileInfoDto userProfileInfoDto = new UserProfileInfoDto();
        UserInfo userInfo = user.getUserInfo();
        userProfileInfoDto.setUsername(user.getUsername());
        userProfileInfoDto.setFirstName(user.getFirstName());
        userProfileInfoDto.setLastName(user.getLastName());
        userProfileInfoDto.setAvatarUrl(userInfo.getAvatarUrl());
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
            userProfileInfoDto.setUserPhotoList(mapToPhotoDtoList(userPhotoList));
        }
        if (!CollectionUtils.isEmpty(userPostList)) {
            userProfileInfoDto.setUserPostList(mapToPostDtoList(userPostList));
        }
        if (!CollectionUtils.isEmpty(userFriendList)) {
            userProfileInfoDto.setUserFriendList(mapToUserFriendDtoList(userFriendList));
        }
        return userProfileInfoDto;
    }

    public static List<UserFriendDto> mapToUserFriendDtoList(List<User> userFriendList) {
        List<UserFriendDto> userFriendDtoList = new ArrayList<>();
        for (User user : userFriendList) {
            userFriendDtoList.add(mapToUserFriendDto(user));
        }
        return userFriendDtoList;
    }

    public static UserFriendDto mapToUserFriendDto(User user) {
        UserFriendDto userFriendDto = new UserFriendDto();
        userFriendDto.setUsername(user.getUsername());
        userFriendDto.setFirstName(user.getFirstName());
        userFriendDto.setLastName(user.getLastName());
        userFriendDto.setAvatarUrl(user.getUserInfo().getAvatarUrl());
        return userFriendDto;
    }

    public static List<UserPhotoDto> mapToPhotoDtoList(List<UserPhoto> userPhotoList) {
        List<UserPhotoDto> userPhotoDtoList = new ArrayList<>();
        for (UserPhoto userPhoto : userPhotoList) {
            userPhotoDtoList.add(mapToPhotoDto(userPhoto));
        }
        return userPhotoDtoList;
    }

    public static UserPhotoDto mapToPhotoDto(UserPhoto userPhoto) {
        UserPhotoDto userPhotoDto = new UserPhotoDto();
        userPhotoDto.setId(userPhoto.getId());
        userPhotoDto.setPhotoUrl(userPhoto.getPhotoUrl());
        userPhotoDto.setNumOfLikes(userPhoto.getNumOfLikes());
        userPhotoDto.setLoadTime(userPhoto.getLoadTime());
        return userPhotoDto;
    }

    public static List<UserPostDto> mapToPostDtoList(List<UserPost> userPostList) {
        List<UserPostDto> userPostDtoList = new ArrayList<>();
        for (UserPost userPost : userPostList) {
            userPostDtoList.add(mapToPostDto(userPost));
        }
        return userPostDtoList;
    }

    public static UserPostDto mapToPostDto(UserPost userPost) {
        UserPostDto userPostDto = new UserPostDto();
        userPostDto.setId(userPost.getId());
        userPostDto.setPhotoUrl(userPost.getPhotoUrl());
        userPostDto.setText(userPost.getText());
        userPostDto.setNumOfLikes(userPost.getNumOfLikes());
        userPostDto.setCreationTime(userPost.getCreationTime());
        return userPostDto;
    }

}
