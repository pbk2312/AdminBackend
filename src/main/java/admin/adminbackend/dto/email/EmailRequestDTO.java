package admin.adminbackend.dto.email;


import admin.adminbackend.domain.EmailCertification;
import admin.adminbackend.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestDTO {

    @NotBlank
    @Email
    private String email;

    public EmailCertification toEmail(String emailCertificationNumber) {
        // 회원 객체를 생성하고 반환
        return EmailCertification.builder()
                .certificationEmail(email)
                .certificationNumber(emailCertificationNumber)
                .build();
    }


}
