package andrew.project.socialNetwork.backend.api.libraries;

import andrew.project.socialNetwork.backend.api.constants.AddToFriendsStatusCode;
import andrew.project.socialNetwork.backend.api.dtos.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MainLib {

    RegStatusDto registration(RegFormDto regFormDto);

    boolean confirm(String key);

    void refreshToken(HttpServletRequest request, HttpServletResponse response);

    UserProfileInfoDto getUserProfileInfo(String username);

    UserFriendsInfoDto getUserFriendsInfo(String username);

    MenuDataDto getMenuData();

    UserPhotoDto addPhoto(MultipartFile file);

    int deletePhoto(Long photoId);

    UserPostDto createPost(MultipartFile file, String text);

    int deletePost(Long postId);

    AddToFriendsStatusCode createFriendRequest(Long userId);

    boolean cancelFriendRequest(Long userId);

    void deleteFriend(Long userId);

    void agreeFriendRequest(Long userId);

    void rejectFriendRequest(Long userId);

    NewsDto getNews(String username);

}
