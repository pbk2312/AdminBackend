package admin.adminbackend.dto.register;

import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDTO {
    private String email;        // 이메일 (VENTURE_PENDING일 때만)
    private MemberRole memberRole;
    public static MemberResponseDTO of(Member member) {
        if (member.getMemberRole() == MemberRole.VENTURE_PENDING) {
            return new MemberResponseDTO(member.getEmail(), member.getMemberRole());
        }
        return new MemberResponseDTO(null, member.getMemberRole());
    }

}
