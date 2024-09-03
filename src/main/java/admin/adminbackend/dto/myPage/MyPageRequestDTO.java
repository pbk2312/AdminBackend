package admin.adminbackend.dto.myPage;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPageRequestDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String password;


}
