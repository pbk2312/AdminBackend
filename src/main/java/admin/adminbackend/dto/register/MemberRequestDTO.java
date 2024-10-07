package admin.adminbackend.dto.register;

import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.MemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

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
    private String certificationNumber; // 인증번호 필드

    @NotBlank
    private String name;  // 이름 추가

    @NotBlank
    private String nickname;  // 닉네임 추가

    @NotBlank
    private String phoneNumber;  // 전화번호 추가

    @NotBlank
    private String address;  // 주소 추가

    private LocalDate dateOfBirth;  // 생일 추가

    public Member toMember(PasswordEncoder passwordEncoder) {
        // 회원 객체를 생성하고 반환
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .memberRole(MemberRole.MEMBER)
                .name(name)  // 이름 추가
                .nickname(nickname)  // 닉네임 추가
                .phoneNumber(phoneNumber)  // 전화번호 추가
                .address(address)  // 주소 추가
                .dateOfBirth(dateOfBirth)  // 생일 추가
                .build();
    }

}