package admin.adminbackend.dto.register;

import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.MemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class MemberRequestDTO {

    @NotNull
    @Email
    @Size(min = 3, max = 50)
    private String email;

    @NotNull
    @Size(min = 3, max = 100)
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

    @NotNull
    private MemberRole memberRole;


    private LocalDate dateOfBirth;  // 생일 추가

    public Member toMember(PasswordEncoder passwordEncoder,MemberRequestDTO memberRequestDTO) {
        log.info("name: {}, memberRole: {}", memberRequestDTO.getName(), memberRequestDTO.getMemberRole());
        // 회원 객체를 생성하고 반환
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(memberRequestDTO.getName())  // 이름 추가
                .phoneNumber(phoneNumber)  // 전화번호 추가
                .dateOfBirth(dateOfBirth)  // 생일 추가
                .memberRole(memberRequestDTO.getMemberRole())
                .build();

    }

}