package admin.adminbackend.dto.email;


import admin.adminbackend.domain.EmailCertification;
import admin.adminbackend.domain.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
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
