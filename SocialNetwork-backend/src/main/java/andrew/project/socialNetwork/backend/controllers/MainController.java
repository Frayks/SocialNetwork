package andrew.project.socialNetwork.backend.controllers;

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
    public ResponseEntity<UserProfileInfoDto> getUserProfileInfo(@RequestParam String username) {
        LOGGER.debug("Method getUserProfileInfo called!");
        UserProfileInfoDto userProfileInfoDto = mainLib.getUserProfileInfo(username);
        if (userProfileInfoDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(userProfileInfoDto);
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
