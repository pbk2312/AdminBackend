package admin.adminbackend.dto.myPage;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPageRequestDTO {

    @NotBlank
    private String email;


}
