package places.service;

import places.model.AuthResponse;
import places.model.LoginRequest;
import places.model.RegisterRequest;
import places.model.SocialLoginRequest;

public interface AuthManager {

    void register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);

    AuthResponse googleLogin(SocialLoginRequest request);

    AuthResponse appleLogin(SocialLoginRequest request);

}
