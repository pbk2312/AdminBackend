package admin.adminbackend.dto.myPage;


import admin.adminbackend.domain.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyPageResponseDTO {

    private String email;
    private MemberRole memberRole;

}
