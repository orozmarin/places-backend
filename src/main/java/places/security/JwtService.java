package places.security;

import places.model.User;

public interface JwtService {

    String generateToken(User user);

    boolean isTokenValid(String token, User user);

    String extractEmail(String token);
}
