package andrew.project.socialNetwork.backend.controllers;

import andrew.project.socialNetwork.backend.api.constants.AddToFriendsStatus;
import andrew.project.socialNetwork.backend.api.dtos.NewsDto;
import andrew.project.socialNetwork.backend.api.dtos.ResponseStatusDto;
import andrew.project.socialNetwork.backend.api.dtos.UserFriendsInfoDto;
import andrew.project.socialNetwork.backend.api.dtos.UserProfileInfoDto;
import andrew.project.socialNetwork.backend.api.libraries.MainLib;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class MainController {

    private static final Logger LOGGER = LogManager.getLogger(MainController.class);

    private MainLib mainLib;

    @GetMapping("/getUserProfileInfo")
    public ResponseEntity<UserProfileInfoDto> getUserProfileInfo(@RequestParam String username) throws Exception {
        LOGGER.debug("Method getUserProfileInfo called!");
        UserProfileInfoDto userProfileInfoDto = mainLib.getUserProfileInfo(username);
        if (userProfileInfoDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(userProfileInfoDto);
    }

    @GetMapping("/getUserFriendsInfo")
    public ResponseEntity<UserFriendsInfoDto> getUserFriendsInfo(@RequestParam String username) throws Exception {
        LOGGER.debug("Method getUserFriendsInfo called!");
        UserFriendsInfoDto userFriendsInfoDto = mainLib.getUserFriendsInfo(username);
        if (userFriendsInfoDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(userFriendsInfoDto);
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

    @GetMapping("/deletePost")
    public ResponseEntity<Void> deletePost(@RequestParam Long postId) {
        LOGGER.debug("Method deletePost called!");
        int count = mainLib.deletePost(postId);
        if (count > 0) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/createFriendRequest")
    public ResponseEntity<ResponseStatusDto> createFriendRequest(@RequestParam Long userId) throws Exception {
        LOGGER.debug("Method createFriendRequest called!");
        AddToFriendsStatus status = mainLib.createFriendRequest(userId);
        if (AddToFriendsStatus.ADDED.equals(status) || AddToFriendsStatus.REQUEST_CREATED.equals(status)) {
            ResponseStatusDto responseStatusDto = new ResponseStatusDto();
            responseStatusDto.setStatus(status.name());
            return ResponseEntity.ok().body(responseStatusDto);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/cancelFriendRequest")
    public ResponseEntity<Void> cancelFriendRequest(@RequestParam Long userId) throws Exception {
        LOGGER.debug("Method cancelFriendRequest called!");
        if (mainLib.cancelFriendRequest(userId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/deleteFriend")
    public ResponseEntity<Void> deleteFriend(@RequestParam Long userId) throws Exception {
        LOGGER.debug("Method deleteFriend called!");
        mainLib.deleteFriend(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/agreeFriendRequest")
    public ResponseEntity<Void> agreeFriendRequest(@RequestParam Long userId) throws Exception {
        LOGGER.debug("Method agreeFriendRequest called!");
        mainLib.agreeFriendRequest(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/rejectFriendRequest")
    public ResponseEntity<Void> rejectFriendRequest(@RequestParam Long userId) throws Exception {
        LOGGER.debug("Method rejectFriendRequest called!");
        mainLib.rejectFriendRequest(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getNews")
    public ResponseEntity<NewsDto> getNews(@RequestParam String username) {
        LOGGER.debug("Method getNews called!");
        NewsDto newsDto = mainLib.getNews(username);
        if (newsDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(newsDto);
    }

    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
