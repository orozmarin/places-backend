package places.rest;

import static places.constants.Constants.REST_URL;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import places.model.UpdateUserRequest;
import places.model.User;
import places.service.UserManager;

@Slf4j
@RestController
@RequestMapping(value = REST_URL)
public class UserController {

    public static final String UPLOAD_PROFILE_IMAGE = "/user/{userId}/upload-profile-image";
    public static final String UPDATE_USER = "/user/{userId}";

    @Autowired
    private UserManager userManager;

    @PostMapping(value = UPLOAD_PROFILE_IMAGE)
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file, @PathVariable String userId)
            throws IOException {
        String url = userManager.updateProfileImage(userId, file);
        return ResponseEntity.ok(url);
    }

    @PatchMapping(value = UPDATE_USER)
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody UpdateUserRequest request) {
        User updated = userManager.updateUser(userId, request);
        return ResponseEntity.ok(updated);
    }
}
