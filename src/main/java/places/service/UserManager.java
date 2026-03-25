package places.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import places.model.UpdateUserRequest;
import places.model.User;

public interface UserManager {
    String updateProfileImage(String userId, MultipartFile file) throws IOException;
    User updateUser(String userId, UpdateUserRequest request);
    List<User> searchUsers(String query, String excludeUserId);
}
