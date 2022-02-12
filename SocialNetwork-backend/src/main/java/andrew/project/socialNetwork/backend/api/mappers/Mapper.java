package andrew.project.socialNetwork.backend.api.mappers;

import andrew.project.socialNetwork.backend.api.dtos.*;
import andrew.project.socialNetwork.backend.api.entities.*;
import javafx.geometry.Pos;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class Mapper {

    public static UserProfileInfoDto mapToUserProfileInfoDto(User user, List<UserPhoto> userPhotoList, List<UserPost> userPostList, List<User> userFriendList, Friends friends) {
        UserProfileInfoDto userProfileInfoDto = new UserProfileInfoDto();
        UserInfo userInfo = user.getUserInfo();
        userProfileInfoDto.setId(user.getId());
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

    public static UserFriendsInfoDto mapToUserFriendsInfoDto(User user, List<User> userFriendList, List<User> userFriendRequestList) {
        UserFriendsInfoDto userFriendsInfoDto = new UserFriendsInfoDto();
        userFriendsInfoDto.setShortUserInfo(mapShortUserInfoDto(user));
        if (!CollectionUtils.isEmpty(userFriendList)) {
            userFriendsInfoDto.setUserFriendList(mapToUserFriendDtoList(userFriendList));
        }
        if (!CollectionUtils.isEmpty(userFriendRequestList)) {
            userFriendsInfoDto.setUserFriendRequestList(mapToUserFriendDtoList(userFriendRequestList));
        }
        return userFriendsInfoDto;
    }

    private static ShortUserInfoDto mapShortUserInfoDto(User user) {
        ShortUserInfoDto shortUserInfoDto = new ShortUserInfoDto();
        shortUserInfoDto.setId(user.getId());
        shortUserInfoDto.setUsername(user.getUsername());
        shortUserInfoDto.setFirstName(user.getFirstName());
        shortUserInfoDto.setLastName(user.getLastName());
        shortUserInfoDto.setAvatarUrl(user.getUserInfo().getAvatarUrl());
        return shortUserInfoDto;
    }

    public static NewsDto mapToNewsDto(List<User> userList, List<UserPost> userPostList) {
        NewsDto newsDto = new NewsDto();
        if (!CollectionUtils.isEmpty(userPostList)) {
            newsDto.setPostList(mapToPostDtoList(userList, userPostList));
        }
        return newsDto;
    }

    private static List<PostDto> mapToPostDtoList(List<User> userList, List<UserPost> userPostList) {
        List<PostDto> postList = new ArrayList<>();
        Map<Long, User> userMap = new HashMap<>();
        for (User user : userList) {
            userMap.put(user.getId(), user);
        }
        for (UserPost userPost : userPostList) {
            User user = userMap.get(userPost.getUserId());
            postList.add(mapToPostDto(userPost, user));
        }
        return postList;
    }

    private static PostDto mapToPostDto(UserPost userPost, User user) {
        PostDto postDto = new PostDto();
        postDto.setId(userPost.getId());
        postDto.setPhotoUrl(userPost.getPhotoUrl());
        postDto.setText(userPost.getText());
        postDto.setNumOfLikes(userPost.getNumOfLikes());
        postDto.setCreationTime(userPost.getCreationTime());
        if (user != null) {
            ShortUserInfoDto shortUserInfoDto = new ShortUserInfoDto();
            shortUserInfoDto.setId(user.getId());
            shortUserInfoDto.setUsername(user.getUsername());
            shortUserInfoDto.setFirstName(user.getFirstName());
            shortUserInfoDto.setLastName(user.getLastName());
            shortUserInfoDto.setAvatarUrl(user.getUserInfo().getAvatarUrl());
            postDto.setShortUserInfo(shortUserInfoDto);
        }
        return postDto;
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
        userFriendDto.setId(user.getId());
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
            userPostDtoList.add(mapToUserPostDto(userPost));
        }
        return userPostDtoList;
    }

    public static UserPostDto mapToUserPostDto(UserPost userPost) {
        UserPostDto userPostDto = new UserPostDto();
        userPostDto.setId(userPost.getId());
        userPostDto.setPhotoUrl(userPost.getPhotoUrl());
        userPostDto.setText(userPost.getText());
        userPostDto.setNumOfLikes(userPost.getNumOfLikes());
        userPostDto.setCreationTime(userPost.getCreationTime());
        return userPostDto;
    }

}
