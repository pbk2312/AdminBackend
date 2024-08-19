package admin.adminbackend.dto.myPage;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChangeDTO {


    @NotBlank
    private String changePassword;

    @NotBlank
    private String changeCheckPassword;


}
