package admin.adminbackend.dto;


import admin.adminbackend.domain.EmailCertification;
import admin.adminbackend.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailResponseDTO {

    private String email;

    public static EmailResponseDTO of(EmailCertification emailCertification) {
        return new EmailResponseDTO(emailCertification.getCertificationEmail());
    }


}
