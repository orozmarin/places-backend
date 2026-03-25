package places.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import places.model.UpdateUserRequest;
import places.model.User;

public interface UserManager {

    String updateProfileImage(String userId, MultipartFile file) throws IOException;

    User updateUser(String userId, UpdateUserRequest request);
}
