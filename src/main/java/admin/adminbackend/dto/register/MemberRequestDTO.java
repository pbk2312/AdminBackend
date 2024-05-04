package admin.adminbackend.dto.register;

import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private MemberRole memberRole;

    @NotBlank
    private String certificationNumber; // 추가: 인증번호 필드

    public Member toMember(PasswordEncoder passwordEncoder) {
        // 회원 객체를 생성하고 반환
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .memberRole(memberRole)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
        // 사용자 로그인 기능 처리하기 위한 메서드
    }

    // 추가: 인증번호를 DTO에 포함시키는 메서드
    public void setCertificationNumber(String certificationNumber) {
        this.certificationNumber = certificationNumber;
    }


}
