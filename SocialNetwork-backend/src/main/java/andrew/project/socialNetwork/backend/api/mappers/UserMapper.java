package andrew.project.socialNetwork.backend.api.mappers;

import andrew.project.socialNetwork.backend.api.dtos.UserProfileInfoDto;
import andrew.project.socialNetwork.backend.api.entities.User;
import andrew.project.socialNetwork.backend.api.entities.UserInfo;

public class UserMapper {

    public static UserProfileInfoDto mapUserToUserProfileInfoDto(User user) {
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
        return userProfileInfoDto;
    }

}
