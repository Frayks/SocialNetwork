package andrew.project.socialNetwork.backend.libraries;

import andrew.project.socialNetwork.backend.api.constants.TokenType;
import andrew.project.socialNetwork.backend.api.dtos.UserProfileInfoDto;
import andrew.project.socialNetwork.backend.api.entities.Friends;
import andrew.project.socialNetwork.backend.api.entities.User;
import andrew.project.socialNetwork.backend.api.entities.UserPhoto;
import andrew.project.socialNetwork.backend.api.entities.UserPost;
import andrew.project.socialNetwork.backend.api.libraries.MainLib;
import andrew.project.socialNetwork.backend.api.mappers.UserMapper;
import andrew.project.socialNetwork.backend.api.services.FriendsService;
import andrew.project.socialNetwork.backend.api.services.UserPhotoService;
import andrew.project.socialNetwork.backend.api.services.UserPostService;
import andrew.project.socialNetwork.backend.api.services.UserService;
import andrew.project.socialNetwork.backend.security.JwtProvider;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class MainLibImpl implements MainLib {

    private static final Logger LOGGER = LogManager.getLogger(MainLibImpl.class);

    private UserService userService;
    private UserPhotoService userPhotoService;
    private UserPostService userPostService;
    private FriendsService friendsService;
    private JwtProvider jwtProvider;

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String refreshToken = jwtProvider.resolveToken(request);
            if (refreshToken == null) {
                throw new Exception("Refresh token is missing!");
            }
            DecodedJWT decodedJWT = jwtProvider.verifyToken(refreshToken, TokenType.REFRESH_TOKEN);
            String username = decodedJWT.getSubject();
            User user = userService.findByUsername(username);
            List<String> roles = new ArrayList<>();
            user.getRoles().forEach(role -> roles.add(role.getRoleName().name()));
            String accessToken = jwtProvider.createAccessToken(user.getUsername(), roles);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", refreshToken);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            addErrorToResponse(response, HttpStatus.FORBIDDEN.value(), e.getMessage());
        }
    }

    @Override
    public UserProfileInfoDto getUserProfileInfo(String username) throws Exception {
        User user = userService.findByUsername(username);
        if (user != null) {
            List<UserPhoto> userPhotoList = userPhotoService.findByUserId(user.getId());
            List<UserPost> userPostList = userPostService.findByUserId(user.getId());
            org.springframework.security.core.userdetails.User userRequester = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userRequesterDbi = userService.findByUsername(userRequester.getUsername());
            List<Friends> friendsList = friendsService.findFriends(user.getId(), 6);
            List<Long> friendsIdList = getFriendsIdList(user.getId(), friendsList);
            List<User> userFriendList = userService.findByIds(friendsIdList);
            Friends friends = friendsService.checkIfFriends(userRequesterDbi.getId(), user.getId());
            return UserMapper.mapToUserProfileInfoDto(user, userPhotoList, userPostList, userFriendList, friends);
        }
        return null;
    }

    @Override
    public void deletePhoto(Long photoId) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(user.getUsername());
        userPhotoService.deleteByIdAndUserId(photoId, userDbi.getId());
    }

    @Override
    public void deletePost(Long postId) {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userDbi = userService.findByUsername(user.getUsername());
        userPostService.deleteByIdAndUserId(postId, userDbi.getId());
    }

    @Override
    public void addErrorToResponse(HttpServletResponse response, int status, String errorMessage) throws IOException {
        response.setStatus(status);
        Map<String, String> error = new HashMap<>();
        error.put("error_message", errorMessage);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }

    private List<Long> getFriendsIdList(Long userId, List<Friends> friendsList) {
        List<Long> friendsIdList = new ArrayList<>();
        for (Friends friends : friendsList) {
            friendsIdList.add((Objects.equals(friends.getFirstUserId(), userId)) ? friends.getSecondUserId() : friends.getFirstUserId());
        }
        return friendsIdList;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserPhotoService(UserPhotoService userPhotoService) {
        this.userPhotoService = userPhotoService;
    }

    @Autowired
    public void setUserPostService(UserPostService userPostService) {
        this.userPostService = userPostService;
    }

    @Autowired
    public void setFriendsService(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @Autowired
    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

}
