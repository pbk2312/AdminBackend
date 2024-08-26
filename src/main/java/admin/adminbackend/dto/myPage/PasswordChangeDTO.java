package admin.adminbackend.dto.myPage;

import lombok.*;

import javax.validation.constraints.NotBlank;

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
