import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginDto {

    @Getter
    @NoArgsConstructor
    public static class TokenResponse {
        private String token_type;
        private String access_token;
        private String refresh_token;
        private Integer expires_in;
        private String scope;
        private Integer refresh_token_expires_in;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenRequest {
        private String grant_type;
        private String client_id;
        private String refresh_token;
    }
}