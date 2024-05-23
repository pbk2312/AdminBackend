package admin.adminbackend.dto.register;

import admin.adminbackend.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDTO {
    private String email;

    public static MemberResponseDTO of(Member member) {
        return new MemberResponseDTO(member.getEmail());
    }

}
