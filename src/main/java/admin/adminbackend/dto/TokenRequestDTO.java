package admin.adminbackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequestDTO {
    private String accessToken;
    private String refreshToken;

}
