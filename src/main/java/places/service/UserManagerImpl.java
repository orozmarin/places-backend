package places.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import places.model.UpdateUserRequest;
import places.model.User;
import places.model.User.UserStatus;
import places.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserManagerImpl implements UserManager {

    private final UserRepository userRepository;

    @Override
    public String updateProfileImage(String userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fileName = userId + "_" + file.getOriginalFilename();
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path filePath = uploadDir.resolve(fileName);
        Files.write(filePath, file.getBytes());
        String url = "/uploads/" + fileName;

        user.setProfileImageUrl(url);
        userRepository.save(user);

        return url;
    }

    @Override
    public User updateUser(String userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getSex() != null) {
            user.setSex(request.getSex());
        }

        if (user.getUsername() != null && user.getDateOfBirth() != null && user.getSex() != null) {
            user.setStatus(UserStatus.ACTIVE);
        }

        return userRepository.save(user);
    }

}
