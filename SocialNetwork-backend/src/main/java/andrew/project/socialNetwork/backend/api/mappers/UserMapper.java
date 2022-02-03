package andrew.project.socialNetwork.backend.api.mappers;

import andrew.project.socialNetwork.backend.api.dtos.UserPhotoDto;
import andrew.project.socialNetwork.backend.api.dtos.UserPostDto;
import andrew.project.socialNetwork.backend.api.dtos.UserProfileInfoDto;
import andrew.project.socialNetwork.backend.api.entities.User;
import andrew.project.socialNetwork.backend.api.entities.UserInfo;
import andrew.project.socialNetwork.backend.api.entities.UserPhoto;
import andrew.project.socialNetwork.backend.api.entities.UserPost;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static UserProfileInfoDto mapToUserProfileInfoDto(User user, List<UserPhoto> userPhotoList, List<UserPost> userPostList) {
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
        if (userPhotoList != null && !userPhotoList.isEmpty()) {
            userProfileInfoDto.setUserPhotoList(mapToPhotoDtoList(userPhotoList));
        }
        if (userPostList != null && !userPostList.isEmpty()) {
            userProfileInfoDto.setUserPostList(mapToPostDtoList(userPostList));
        }
        return userProfileInfoDto;
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
