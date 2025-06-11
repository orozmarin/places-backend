package places.repository.qdls;

import places.model.User;

public interface JwtServiceCustom {

    String generateToken(User user);

    boolean isTokenValid(String token, User user);

    String extractEmail(String token);
}
