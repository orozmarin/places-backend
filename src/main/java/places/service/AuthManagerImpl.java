package places.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import places.model.AuthResponse;
import places.model.LoginRequest;
import places.model.RegisterRequest;
import places.model.SocialLoginRequest;
import places.model.User;
import places.model.User.AuthProvider;
import places.model.User.UserStatus;
import places.repository.UserRepository;
import places.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthManagerImpl implements AuthManager {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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

    private static final String GOOGLE_JWKS_URI = "https://www.googleapis.com/oauth2/v3/certs";
    private static final String APPLE_JWKS_URI = "https://appleid.apple.com/auth/keys";

    @Override
    public AuthResponse socialLogin(SocialLoginRequest request) {
        String jwksUri = request.getProvider() == AuthProvider.GOOGLE ? GOOGLE_JWKS_URI : APPLE_JWKS_URI;

        JwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
        Jwt jwt = decoder.decode(request.getIdToken());

        String email = jwt.getClaimAsString("email");
        if (email == null) {
            throw new RuntimeException("Email not present in ID token");
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            String firstName = jwt.getClaimAsString("given_name");
            String lastName = jwt.getClaimAsString("family_name");
            if (firstName == null) firstName = jwt.getClaimAsString("name");

            User newUser = User.builder()
                    .email(email)
                    .firstName(firstName != null ? firstName : "")
                    .lastName(lastName != null ? lastName : "")
                    .status(UserStatus.ACTIVE)
                    .authProvider(request.getProvider())
                    .build();
            return userRepository.save(newUser);
        });

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user);
    }

    @Override
    public void register(RegisterRequest request) {
        boolean exists = userRepository.existsByEmail(request.getEmail());
        if (exists) {
            throw new RuntimeException("Email already in use");
        }

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
                throw new RuntimeException("Username already taken");
            });
        }

        String tag = java.util.UUID.randomUUID().toString().replaceAll("-", "").substring(0, 4).toUpperCase();

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
}
