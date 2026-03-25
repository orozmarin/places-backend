package places.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import places.model.User.AuthProvider;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialLoginRequest {

    private String idToken;
    private AuthProvider provider;
}
