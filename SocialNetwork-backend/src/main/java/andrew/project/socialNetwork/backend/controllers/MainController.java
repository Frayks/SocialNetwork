package andrew.project.socialNetwork.backend.controllers;

import andrew.project.socialNetwork.backend.api.constants.AddToFriendsStatusCode;
import andrew.project.socialNetwork.backend.api.dtos.*;
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

    @GetMapping("/getUserProfileInfo")
    public ResponseEntity<UserProfileInfoDto> getUserProfileInfo(@RequestParam String username) {
        LOGGER.debug("Method getUserProfileInfo called!");
        UserProfileInfoDto userProfileInfoDto = mainLib.getUserProfileInfo(username);
        if (userProfileInfoDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(userProfileInfoDto);
    }

    @GetMapping("/getUserFriendsInfo")
    public ResponseEntity<UserFriendsInfoDto> getUserFriendsInfo(@RequestParam String username) {
        LOGGER.debug("Method getUserFriendsInfo called!");
        UserFriendsInfoDto userFriendsInfoDto = mainLib.getUserFriendsInfo(username);
        if (userFriendsInfoDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(userFriendsInfoDto);
    }

    @GetMapping("/getMenuData")
    public ResponseEntity<MenuDataDto> getMenuData() {
        LOGGER.debug("Method getMenuData called!");
        MenuDataDto menuDataDto = mainLib.getMenuData();
        if (menuDataDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(menuDataDto);
    }

    @PostMapping("/addPhoto")
    public ResponseEntity<UserPhotoDto> addPhoto(@RequestParam(value = "photo") MultipartFile file) {
        LOGGER.debug("Method addPhoto called!");
        UserPhotoDto userPhotoDto = mainLib.addPhoto(file);
        if (userPhotoDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().body(userPhotoDto);
    }

    @GetMapping("/deletePhoto")
    public ResponseEntity<Void> deletePhoto(@RequestParam Long photoId) {
        LOGGER.debug("Method deletePhoto called!");
        int count = mainLib.deletePhoto(photoId);
        if (count > 0) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/changePhotoLike")
    public ResponseEntity<Void> changePhotoLike(@RequestParam Long photoId) {
        LOGGER.debug("Method changePhotoLike called!");
        boolean success = mainLib.changePhotoLike(photoId);
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/createPost")
    public ResponseEntity<UserPostDto> createPost(@RequestParam(value = "photo", required = false) MultipartFile file, @RequestParam(value = "text", required = false) String text) {
        LOGGER.debug("Method createPost called!");
        UserPostDto userPostDto = mainLib.createPost(file, text);
        if (userPostDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().body(userPostDto);
    }

    @GetMapping("/deletePost")
    public ResponseEntity<Void> deletePost(@RequestParam Long postId) {
        LOGGER.debug("Method deletePost called!");
        int count = mainLib.deletePost(postId);
        if (count > 0) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getUserPostList")
    public ResponseEntity<List<UserPostDto>> getUserPostList(@RequestParam String username, @RequestParam(required = false) String beforeTime) {
        LOGGER.debug("Method getUserPostList called!");
        List<UserPostDto> userPostDtoList = mainLib.getUserPostList(username, beforeTime);
        if (userPostDtoList == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(userPostDtoList);
    }

    @GetMapping("/getPostList")
    public ResponseEntity<List<PostDto>> getPostList(@RequestParam(required = false) String beforeTime) {
        LOGGER.debug("Method getPostList called!");
        List<PostDto> postDtoList = mainLib.getPostList(beforeTime);
        if (postDtoList == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(postDtoList);
    }

    @GetMapping("/changePostLike")
    public ResponseEntity<Void> changePostLike(@RequestParam Long postId) {
        LOGGER.debug("Method changePostLike called!");
        boolean success = mainLib.changePostLike(postId);
        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/createFriendRequest")
    public ResponseEntity<ResponseStatusDto> createFriendRequest(@RequestParam Long userId) {
        LOGGER.debug("Method createFriendRequest called!");
        AddToFriendsStatusCode status = mainLib.createFriendRequest(userId);
        if (AddToFriendsStatusCode.ADDED.equals(status) || AddToFriendsStatusCode.REQUEST_CREATED.equals(status)) {
            ResponseStatusDto responseStatusDto = new ResponseStatusDto();
            responseStatusDto.setStatus(status.name());
            return ResponseEntity.ok().body(responseStatusDto);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/cancelFriendRequest")
    public ResponseEntity<Void> cancelFriendRequest(@RequestParam Long userId) {
        LOGGER.debug("Method cancelFriendRequest called!");
        if (mainLib.cancelFriendRequest(userId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/deleteFriend")
    public ResponseEntity<Void> deleteFriend(@RequestParam Long userId) {
        LOGGER.debug("Method deleteFriend called!");
        mainLib.deleteFriend(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/agreeFriendRequest")
    public ResponseEntity<Void> agreeFriendRequest(@RequestParam Long userId) {
        LOGGER.debug("Method agreeFriendRequest called!");
        mainLib.agreeFriendRequest(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/rejectFriendRequest")
    public ResponseEntity<Void> rejectFriendRequest(@RequestParam Long userId) {
        LOGGER.debug("Method rejectFriendRequest called!");
        mainLib.rejectFriendRequest(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getSettings")
    public ResponseEntity<SettingsDto> getSettings() {
        LOGGER.debug("Method getSettings called!");
        SettingsDto settingsDto = mainLib.getSettings();
        if (settingsDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(settingsDto);
    }

    @PostMapping("/changeBasicSettings")
    public ResponseEntity<FormStatusDto> changeBasicSettings(@RequestParam(value = "photo", required = false) MultipartFile image, @RequestParam(value = "basicSettings") String basicSettingsJson) {
        LOGGER.debug("Method changeBasicSettings called!");
        FormStatusDto formStatusDto = mainLib.changeBasicSettings(image, basicSettingsJson);
        return ResponseEntity.ok().body(formStatusDto);
    }

    @PostMapping("/changeAdditionalSettings")
    public ResponseEntity<FormStatusDto> changeAdditionalSettings(@RequestBody AdditionalSettingsDto additionalSettingsDto) {
        LOGGER.debug("Method changeAdditionalSettings called!");
        FormStatusDto formStatusDto = mainLib.changeAdditionalSettings(additionalSettingsDto);
        return ResponseEntity.ok().body(formStatusDto);
    }

    @GetMapping("/searchUsers")
    public ResponseEntity<SearchResultDto> searchUsers(@RequestParam String searchRequest) {
        LOGGER.debug("Method searchUsers called!");
        SearchResultDto searchResultDto = mainLib.searchUsers(searchRequest);
        if (searchResultDto == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(searchResultDto);
    }

    @PostMapping("/registration")
    public ResponseEntity<FormStatusDto> registration(@RequestBody RegFormDto regFormDto) {
        LOGGER.debug("Method registration called!");
        FormStatusDto formStatusDto = mainLib.registration(regFormDto);
        return ResponseEntity.ok().body(formStatusDto);
    }

    @GetMapping("/confirm")
    public ResponseEntity<Void> confirm(@RequestParam String key) {
        LOGGER.debug("Method confirm called!");
        if (mainLib.confirm(key)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/restore")
    public ResponseEntity<Void> restore(@RequestParam String email) {
        LOGGER.debug("Method restore called!");
        if (mainLib.restore(email)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<FormStatusDto> resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        LOGGER.debug("Method resetPassword called!");
        LOGGER.info(resetPasswordRequestDto);
        FormStatusDto formStatusDto = mainLib.resetPassword(resetPasswordRequestDto);
        return ResponseEntity.ok().body(formStatusDto);
    }

    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.debug("Method refreshToken called!");
        mainLib.refreshToken(request, response);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.debug("Method logout called!");
        // TODO Необхідно реалізувати чорний список для токенів
    }

    @Autowired
    public void setMainLib(MainLib mainLib) {
        this.mainLib = mainLib;
    }

}
