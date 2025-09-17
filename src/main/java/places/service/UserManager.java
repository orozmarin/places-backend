package places.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface UserManager {

    String updateProfileImage(String userId, MultipartFile file) throws IOException;
}
