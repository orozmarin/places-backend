package places.service;

import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import places.model.AuthResponse;
import places.model.LoginRequest;
import places.model.RegisterRequest;
import places.model.SocialLoginRequest;
import places.model.User;
import places.model.User.UserStatus;
import places.repository.UserRepository;
import places.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthManagerImpl implements AuthManager {

    private static final String GOOGLE_TOKENINFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user);
    }

    @Override
    public void register(RegisterRequest request) {
        boolean exists = userRepository.existsByEmail(request.getEmail());
        if (exists) {
            throw new RuntimeException("Email already in use");
        }

        String tag = generateUniqueTag();

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .tag(tag)
                .status(UserStatus.ACTIVE)
                .sex(request.getSex())
                .dateOfBirth(request.getDateOfBirth())
                .build();

        userRepository.save(user);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AuthResponse socialLogin(SocialLoginRequest request) {
        Map<String, Object> claims = restTemplate.getForObject(
                GOOGLE_TOKENINFO_URL + request.getIdToken(), Map.class);

        if (claims == null) {
            throw new RuntimeException("Failed to verify Google token");
        }

        String email = (String) claims.get("email");
        if (email == null) {
            throw new RuntimeException("No email in Google token");
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            String firstName = (String) claims.getOrDefault("given_name", "");
            String lastName = (String) claims.getOrDefault("family_name", "");
            String picture = (String) claims.get("picture");
            String tag = generateUniqueTag();

            User newUser = User.builder()
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .tag(tag)
                    .profileImageUrl(picture)
                    .status(UserStatus.WAITING_FIRST_LOGIN)
                    .build();

            return userRepository.save(newUser);
        });

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user);
    }

    private String generateUniqueTag() {
        Random random = new Random();
        String tag;
        do {
            tag = String.format("%04d", random.nextInt(10000));
        } while (userRepository.existsByTag(tag));
        return tag;
    }
}
