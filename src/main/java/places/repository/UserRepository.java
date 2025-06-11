package places.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import places.model.User;

public interface UserRepository  extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
