package admin.adminbackend.dto.myPage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String nowPassword;

    @NotBlank
    private String changePassword;

    @NotBlank
    private String changeCheckPassword;


}
