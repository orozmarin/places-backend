package places.service;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import places.model.AuthResponse;
import places.model.LoginRequest;
import places.model.RegisterRequest;
import places.model.SocialLoginRequest;
import places.model.User;
import places.model.User.AuthProvider;
import places.model.User.UserStatus;
import places.repository.UserRepository;
import places.security.JwtService;

import java.net.URL;
import java.util.Map;
import java.util.UUID;

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

    @Override
    public void register(RegisterRequest request) {
        boolean exists = userRepository.existsByEmail(request.getEmail());
        if (exists) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status(UserStatus.ACTIVE)
                .authProvider(AuthProvider.EMAIL)
                .sex(request.getSex())
                .dateOfBirth(request.getDateOfBirth())
                .build();

        userRepository.save(user);
    }

    @Override
    public AuthResponse googleLogin(SocialLoginRequest request) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + request.getIdToken();
            @SuppressWarnings("unchecked")
            Map<String, Object> tokenInfo = restTemplate.getForObject(url, Map.class);

            if (tokenInfo == null || tokenInfo.containsKey("error")) {
                throw new RuntimeException("Invalid Google token");
            }

            String email = (String) tokenInfo.get("email");
            String firstName = (String) tokenInfo.get("given_name");
            String lastName = (String) tokenInfo.get("family_name");
            String pictureUrl = (String) tokenInfo.get("picture");

            User user = findOrCreateSocialUser(email, firstName, lastName, pictureUrl, AuthProvider.GOOGLE);
            String token = jwtService.generateToken(user);
            return new AuthResponse(token, user);
        } catch (Exception e) {
            throw new RuntimeException("Google login failed: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse appleLogin(SocialLoginRequest request) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(request.getIdToken());
            String kid = signedJWT.getHeader().getKeyID();

            JWKSet jwkSet = JWKSet.load(new URL("https://appleid.apple.com/auth/keys"));
            RSAKey rsaKey = (RSAKey) jwkSet.getKeyByKeyId(kid);

            if (rsaKey == null) {
                throw new RuntimeException("Apple public key not found for kid: " + kid);
            }

            RSASSAVerifier verifier = new RSASSAVerifier(rsaKey);
            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Invalid Apple token signature");
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            String email = claims.getStringClaim("email");

            User user = findOrCreateSocialUser(email, null, null, null, AuthProvider.APPLE);
            String token = jwtService.generateToken(user);
            return new AuthResponse(token, user);
        } catch (Exception e) {
            throw new RuntimeException("Apple login failed: " + e.getMessage());
        }
    }

    private User findOrCreateSocialUser(String email, String firstName, String lastName,
                                         String profileImageUrl, AuthProvider authProvider) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            String randomSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            User newUser = User.builder()
                    .email(email)
                    .firstName(firstName != null ? firstName : "")
                    .lastName(lastName != null ? lastName : "")
                    .profileImageUrl(profileImageUrl)
                    .status(UserStatus.ACTIVE)
                    .authProvider(authProvider)
                    .build();
            return userRepository.save(newUser);
        });
    }
}
