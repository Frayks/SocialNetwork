package andrew.project.socialNetwork.backend.controllers;

import andrew.project.socialNetwork.backend.api.constants.AddToFriendsStatusCode;
import andrew.project.socialNetwork.backend.api.dtos.*;
import andrew.project.socialNetwork.backend.api.libraries.AuthLib;
import andrew.project.socialNetwork.backend.api.libraries.MainLib;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MainController {

    private static final Logger LOGGER = LogManager.getLogger(MainController.class);

    private MainLib mainLib;
    private AuthLib authLib;

    @GetMapping("/getUserProfileInfo")
    public ResponseEntity<UserProfileInfoDto> getUserProfileInfo(HttpServletRequest request, @RequestParam String username) {
        LOGGER.debug(request.getRemoteAddr() + " -> getUserProfileInfo");
        UserProfileInfoDto userProfileInfoDto = mainLib.getUserProfileInfo(username);
        if (userProfileInfoDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(userProfileInfoDto);
    }

    @GetMapping("/getUserFriendsInfo")
    public ResponseEntity<UserFriendsInfoDto> getUserFriendsInfo(HttpServletRequest request, @RequestParam String username) {
        LOGGER.debug(request.getRemoteAddr() + " -> getUserFriendsInfo");
        UserFriendsInfoDto userFriendsInfoDto = mainLib.getUserFriendsInfo(username);
        if (userFriendsInfoDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(userFriendsInfoDto);
    }

    @GetMapping("/getMenuData")
    public ResponseEntity<MenuDataDto> getMenuData(HttpServletRequest request) {
        LOGGER.debug(request.getRemoteAddr() + " -> getMenuData");
        MenuDataDto menuDataDto = mainLib.getMenuData();
        if (menuDataDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(menuDataDto);
    }

    @PostMapping("/addPhoto")
    public ResponseEntity<UserPhotoDto> addPhoto(HttpServletRequest request, @RequestParam(value = "photo") MultipartFile file) {
        LOGGER.debug(request.getRemoteAddr() + " -> addPhoto");
        UserPhotoDto userPhotoDto = mainLib.addPhoto(file);
        if (userPhotoDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().body(userPhotoDto);
    }

    @GetMapping("/deletePhoto")
    public ResponseEntity<Void> deletePhoto(HttpServletRequest request, @RequestParam Long photoId) {
        LOGGER.debug(request.getRemoteAddr() + " -> deletePhoto");
        int count = mainLib.deletePhoto(photoId);
        if (count > 0) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/changePhotoLike")
    public ResponseEntity<Void> changePhotoLike(HttpServletRequest request, @RequestParam Long photoId) {
        LOGGER.debug(request.getRemoteAddr() + " -> changePhotoLike");
        boolean success = mainLib.changePhotoLike(photoId);
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/createPost")
    public ResponseEntity<FormStatusDto> createPost(HttpServletRequest request, @RequestParam(value = "postPhoto", required = false) MultipartFile postPhoto, @RequestParam(value = "postText", required = false) String postText) {
        LOGGER.debug(request.getRemoteAddr() + " -> createPost");
        FormStatusDto formStatusDto = mainLib.createPost(postPhoto, postText);
        return ResponseEntity.ok().body(formStatusDto);
    }

    @GetMapping("/deletePost")
    public ResponseEntity<Void> deletePost(HttpServletRequest request, @RequestParam Long postId) {
        LOGGER.debug(request.getRemoteAddr() + " -> deletePost");
        int count = mainLib.deletePost(postId);
        if (count > 0) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getUserPostList")
    public ResponseEntity<List<UserPostDto>> getUserPostList(HttpServletRequest request, @RequestParam String username, @RequestParam(required = false) String beforeTime) {
        LOGGER.debug(request.getRemoteAddr() + " -> getUserPostList");
        List<UserPostDto> userPostDtoList = mainLib.getUserPostList(username, beforeTime);
        if (userPostDtoList == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(userPostDtoList);
    }

    @GetMapping("/getPostListBlock")
    public ResponseEntity<List<PostDto>> getPostListBlock(HttpServletRequest request, @RequestParam(required = false) String beforeTime) {
        LOGGER.debug(request.getRemoteAddr() + " -> getPostListBlock");
        List<PostDto> postDtoList = mainLib.getPostListBlock(beforeTime);
        if (postDtoList == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(postDtoList);
    }

    @GetMapping("/changePostLike")
    public ResponseEntity<Void> changePostLike(HttpServletRequest request, @RequestParam Long postId) {
        LOGGER.debug(request.getRemoteAddr() + " -> changePostLike");
        boolean success = mainLib.changePostLike(postId);
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/createFriendRequest")
    public ResponseEntity<ResponseStatusDto> createFriendRequest(HttpServletRequest request, @RequestParam Long userId) {
        LOGGER.debug(request.getRemoteAddr() + " -> createFriendRequest");
        AddToFriendsStatusCode status = mainLib.createFriendRequest(userId);
        if (AddToFriendsStatusCode.ADDED.equals(status) || AddToFriendsStatusCode.REQUEST_CREATED.equals(status)) {
            ResponseStatusDto responseStatusDto = new ResponseStatusDto();
            responseStatusDto.setStatus(status.name());
            return ResponseEntity.ok().body(responseStatusDto);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/cancelFriendRequest")
    public ResponseEntity<Void> cancelFriendRequest(HttpServletRequest request, @RequestParam Long userId) {
        LOGGER.debug(request.getRemoteAddr() + " -> cancelFriendRequest");
        if (mainLib.cancelFriendRequest(userId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/deleteFriend")
    public ResponseEntity<Void> deleteFriend(HttpServletRequest request, @RequestParam Long userId) {
        LOGGER.debug(request.getRemoteAddr() + " -> deleteFriend");
        mainLib.deleteFriend(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/agreeFriendRequest")
    public ResponseEntity<Void> agreeFriendRequest(HttpServletRequest request, @RequestParam Long userId) {
        LOGGER.debug(request.getRemoteAddr() + " -> agreeFriendRequest");
        mainLib.agreeFriendRequest(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/rejectFriendRequest")
    public ResponseEntity<Void> rejectFriendRequest(HttpServletRequest request, @RequestParam Long userId) {
        LOGGER.debug(request.getRemoteAddr() + " -> rejectFriendRequest");
        mainLib.rejectFriendRequest(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getSettings")
    public ResponseEntity<SettingsDto> getSettings(HttpServletRequest request) {
        LOGGER.debug(request.getRemoteAddr() + " -> getSettings");
        SettingsDto settingsDto = mainLib.getSettings();
        if (settingsDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(settingsDto);
    }

    @PostMapping("/changeBasicSettings")
    public ResponseEntity<FormStatusDto> changeBasicSettings(HttpServletRequest request, @RequestParam(value = "photo", required = false) MultipartFile image, @RequestParam(value = "basicSettings") String basicSettingsJson) {
        LOGGER.debug(request.getRemoteAddr() + " -> changeBasicSettings");
        FormStatusDto formStatusDto = mainLib.changeBasicSettings(image, basicSettingsJson);
        return ResponseEntity.ok().body(formStatusDto);
    }

    @PostMapping("/changeAdditionalSettings")
    public ResponseEntity<FormStatusDto> changeAdditionalSettings(HttpServletRequest request, @RequestBody AdditionalSettingsDto additionalSettingsDto) {
        LOGGER.debug(request.getRemoteAddr() + " -> changeAdditionalSettings");
        FormStatusDto formStatusDto = mainLib.changeAdditionalSettings(additionalSettingsDto);
        return ResponseEntity.ok().body(formStatusDto);
    }

    @GetMapping("/searchUsers")
    public ResponseEntity<SearchResultDto> searchUsers(HttpServletRequest request, @RequestParam String searchRequest) {
        LOGGER.debug(request.getRemoteAddr() + " -> searchUsers");
        SearchResultDto searchResultDto = mainLib.searchUsers(searchRequest);
        if (searchResultDto == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(searchResultDto);
    }

    @GetMapping("/getChatInfoData")
    public ResponseEntity<ChatInfoDataDto> getChatInfoData(HttpServletRequest request, @RequestParam(required = false) String chatWith) {
        LOGGER.debug(request.getRemoteAddr() + " -> getChatInfoData");
        ChatInfoDataDto chatInfoData = mainLib.getChatInfoData(chatWith);
        if (chatInfoData == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(chatInfoData);
    }

    @GetMapping("/getChatMessageListBlock")
    public ResponseEntity<List<ChatMessageDto>> getChatMessageListBlock(HttpServletRequest request, @RequestParam() Long chatId, @RequestParam(required = false) String beforeTime) {
        LOGGER.debug(request.getRemoteAddr() + " -> getChatMessageListBlock");
        List<ChatMessageDto> chatMessageDtoList = mainLib.getChatMessageListBlock(chatId, beforeTime);
        if (chatMessageDtoList == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(chatMessageDtoList);
    }

    @GetMapping("/getWebSocketSessionKey")
    public ResponseEntity<WebSocketSessionKeyDto> getWebSocketSessionKey(HttpServletRequest request) {
        LOGGER.debug(request.getRemoteAddr() + " -> getWebSocketSessionKey");
        WebSocketSessionKeyDto webSocketSessionKey = authLib.getWebSocketSessionKey();
        if (webSocketSessionKey == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(webSocketSessionKey);
    }

    @PostMapping("/registration")
    public ResponseEntity<FormStatusDto> registration(HttpServletRequest request, @RequestBody RegFormDto regFormDto) {
        LOGGER.debug(request.getRemoteAddr() + " -> registration");
        FormStatusDto formStatusDto = authLib.registration(regFormDto);
        return ResponseEntity.ok().body(formStatusDto);
    }

    @GetMapping("/confirm")
    public ResponseEntity<Void> confirm(HttpServletRequest request, @RequestParam String key) {
        LOGGER.debug(request.getRemoteAddr() + " -> confirm");
        if (authLib.confirm(key)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/restore")
    public ResponseEntity<FormStatusDto> restore(HttpServletRequest request, @RequestParam String email) {
        LOGGER.debug(request.getRemoteAddr() + " -> restore");
        FormStatusDto formStatusDto = authLib.restore(email);
        return ResponseEntity.ok().body(formStatusDto);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<FormStatusDto> resetPassword(HttpServletRequest request, @RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        LOGGER.debug(request.getRemoteAddr() + " -> resetPassword");
        FormStatusDto formStatusDto = authLib.resetPassword(resetPasswordRequestDto);
        return ResponseEntity.ok().body(formStatusDto);
    }

    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.debug(request.getRemoteAddr() + " -> refreshToken");
        authLib.refreshToken(request, response);
    }

    @PostMapping("/deleteAccount")
    public ResponseEntity<FormStatusDto> deleteAccount(HttpServletRequest request, @RequestBody String password) {
        LOGGER.debug(request.getRemoteAddr() + " -> deleteAccount");
        FormStatusDto formStatusDto = authLib.deleteAccount(password);
        return ResponseEntity.ok().body(formStatusDto);
    }

    @Autowired
    public void setMainLib(MainLib mainLib) {
        this.mainLib = mainLib;
    }

    @Autowired
    public void setAuthLib(AuthLib authLib) {
        this.authLib = authLib;
    }

}
