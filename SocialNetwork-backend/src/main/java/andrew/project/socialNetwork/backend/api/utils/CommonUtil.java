package andrew.project.socialNetwork.backend.api.utils;

import andrew.project.socialNetwork.backend.api.entities.Friends;
import andrew.project.socialNetwork.backend.api.entities.UserChat;
import andrew.project.socialNetwork.backend.api.entities.UserPhoto;
import andrew.project.socialNetwork.backend.api.entities.UserPost;
import andrew.project.socialNetwork.backend.libraries.MainLibImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class CommonUtil {

    private static final Logger LOGGER = LogManager.getLogger(CommonUtil.class);

    public static List<Long> getFriendIdList(Long userId, List<Friends> friendsList) {
        List<Long> friendIdList = new ArrayList<>();
        for (Friends friends : friendsList) {
            friendIdList.add(Objects.equals(friends.getFirstUserId(), userId) ? friends.getSecondUserId() : friends.getFirstUserId());
        }
        return friendIdList;
    }

    public static List<Long> getChatMemberIdList(Long userId, List<UserChat> userChatList) {
        List<Long> chatMemberIdList = new ArrayList<>();
        for (UserChat userChat : userChatList) {
            chatMemberIdList.add(getChatMemberId(userId, userChat));
        }
        chatMemberIdList.add(userId);
        return chatMemberIdList;
    }

    public static List<Long> getChatMemberIdList(Long userId, UserChat userChat) {
        List<Long> chatMemberIdList = new ArrayList<>();
        chatMemberIdList.add(getChatMemberId(userId, userChat));
        chatMemberIdList.add(userId);
        return chatMemberIdList;
    }

    public static Long getChatMemberId(Long userId, UserChat userChat) {
        return Objects.equals(userChat.getFirstUserId(), userId) ? userChat.getSecondUserId() : userChat.getFirstUserId();
    }

    public static List<Long> getFriendRequestIdList(List<Friends> friendsList) {
        List<Long> friendsIdList = new ArrayList<>();
        for (Friends friends : friendsList) {
            friendsIdList.add(friends.getFirstUserId());
        }
        return friendsIdList;
    }

    public static List<Long> getPhotoIdList(List<UserPhoto> userPhotoList) {
        List<Long> photoIdList = new ArrayList<>();
        for (UserPhoto userPhoto : userPhotoList) {
            photoIdList.add(userPhoto.getId());
        }
        return photoIdList;
    }

    public static List<String> getPhotoNameList(List<UserPhoto> userPhotoList) {
        List<String> photoNameList = new ArrayList<>();
        for (UserPhoto userPhoto : userPhotoList) {
            photoNameList.add(userPhoto.getName());
        }
        return photoNameList;
    }

    public static List<String> getPostPhotoNameList(List<UserPost> userPostList) {
        List<String> postPhotoNameList = new ArrayList<>();
        for (UserPost userPost : userPostList) {
            if (userPost.getPhotoName() != null)
                postPhotoNameList.add(userPost.getPhotoName());
        }
        return postPhotoNameList;
    }

    public static List<Long> getPostIdList(List<UserPost> userPostList) {
        List<Long> postIdList = new ArrayList<>();
        for (UserPost userPost : userPostList) {
            postIdList.add(userPost.getId());
        }
        return postIdList;
    }

    public static boolean isChatMember(UserChat userChat, Long userId) {
        return userChat.getFirstUserId().equals(userId) || userChat.getSecondUserId().equals(userId);
    }

    public static void addErrorToResponse(HttpServletResponse response, int status, String errorMessage) {
        response.setStatus(status);
        Map<String, String> error = new HashMap<>();
        error.put("error_message", errorMessage);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

}
