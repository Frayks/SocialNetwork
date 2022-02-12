package andrew.project.socialNetwork.backend.api.libraries;

import andrew.project.socialNetwork.backend.api.constants.AddToFriendsStatus;
import andrew.project.socialNetwork.backend.api.dtos.NewsDto;
import andrew.project.socialNetwork.backend.api.dtos.UserFriendsInfoDto;
import andrew.project.socialNetwork.backend.api.dtos.UserProfileInfoDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MainLib {

    void addErrorToResponse(HttpServletResponse response, int status, String errorMessage) throws IOException;

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    UserProfileInfoDto getUserProfileInfo(String username) throws Exception;

    UserFriendsInfoDto getUserFriendsInfo(String username) throws Exception;

    int deletePhoto(Long photoId);

    int deletePost(Long postId);

    AddToFriendsStatus createFriendRequest(Long userId) throws Exception;

    boolean cancelFriendRequest(Long userId) throws Exception;

    void deleteFriend(Long userId) throws Exception;

    void agreeFriendRequest(Long userId) throws Exception;

    void rejectFriendRequest(Long userId) throws Exception;

    NewsDto getNews(String username);
}
