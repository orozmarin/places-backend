package places.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import places.model.User;
import places.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserManagerImpl implements UserManager {

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

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
    public List<User> searchUsers(String query, String excludeUserId) {
        Query mongoQuery = new Query();
        mongoQuery.addCriteria(new Criteria().orOperator(
                Criteria.where("firstName").regex(query, "i"),
                Criteria.where("lastName").regex(query, "i"),
                Criteria.where("email").regex(query, "i"),
                Criteria.where("username").regex(query, "i"),
                Criteria.where("tag").regex(query, "i")
        ));
        mongoQuery.addCriteria(Criteria.where("_id").ne(excludeUserId));
        return mongoTemplate.find(mongoQuery, User.class);
    }
}
