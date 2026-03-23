package places.rest;

import static places.constants.Constants.REST_URL;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import places.model.AuthResponse;
import places.model.LoginRequest;
import places.model.RegisterRequest;
import places.model.SocialLoginRequest;
import places.service.AuthManager;

@Slf4j
@RestController
@RequestMapping(value = REST_URL)
public class AuthController {

    public static final String REGISTER = "/auth/register";
    public static final String LOGIN = "/auth/login";
    public static final String GOOGLE_LOGIN = "/auth/google";
    public static final String APPLE_LOGIN = "/auth/apple";

    @Autowired
    private AuthManager authManager;

    @PostMapping(value = REGISTER)
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        authManager.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = LOGIN)
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authManager.login(request));
    }

    @PostMapping(value = GOOGLE_LOGIN)
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody SocialLoginRequest request) {
        return ResponseEntity.ok(authManager.googleLogin(request));
    }

    @PostMapping(value = APPLE_LOGIN)
    public ResponseEntity<AuthResponse> appleLogin(@RequestBody SocialLoginRequest request) {
        return ResponseEntity.ok(authManager.appleLogin(request));
    }
}
