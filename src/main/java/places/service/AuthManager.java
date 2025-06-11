package places.service;

import places.model.AuthResponse;
import places.model.LoginRequest;
import places.model.RegisterRequest;

public interface AuthManager {

    void register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);

}
