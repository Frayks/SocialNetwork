package andrew.project.socialNetwork.backend.api.libraries;

import andrew.project.socialNetwork.backend.api.constants.AddToFriendsStatusCode;
import andrew.project.socialNetwork.backend.api.dtos.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface MainLib {

    FormStatusDto registration(RegFormDto regFormDto);

    boolean confirm(String key);

    boolean restore(String email);

    FormStatusDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);

    void refreshToken(HttpServletRequest request, HttpServletResponse response);

    UserProfileInfoDto getUserProfileInfo(String username);

    UserFriendsInfoDto getUserFriendsInfo(String username);

    MenuDataDto getMenuData();

    SettingsDto getSettings();

    FormStatusDto changeBasicSettings(MultipartFile image, String basicSettingsJson);

    FormStatusDto changeAdditionalSettings(AdditionalSettingsDto additionalSettingsDto);

    SearchResultDto searchUsers(String searchRequest);

    UserPhotoDto addPhoto(MultipartFile file);

    int deletePhoto(Long photoId);

    FormStatusDto createPost(MultipartFile image, String text);

    int deletePost(Long postId);

    boolean changePostLike(Long postId);

    boolean changePhotoLike(Long photoId);

    List<UserPostDto> getUserPostList(String username, String beforeTimeStr);

    List<PostDto> getPostListBlock(String beforeTime);

    AddToFriendsStatusCode createFriendRequest(Long userId);

    boolean cancelFriendRequest(Long userId);

    void deleteFriend(Long userId);

    void agreeFriendRequest(Long userId);

    void rejectFriendRequest(Long userId);

    ChatInfoDataDto getChatInfoData(String chatWith);

    List<ChatMessageDto> getChatMessageListBlock(Long chatId, String beforeTime);

    WebSocketSessionKeyDto getWebSocketSessionKey();

}
