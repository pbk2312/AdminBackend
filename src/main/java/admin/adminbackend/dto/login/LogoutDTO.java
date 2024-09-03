package admin.adminbackend.dto.login;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LogoutDTO {

    @NotBlank
    @Email
    private String email;

}
