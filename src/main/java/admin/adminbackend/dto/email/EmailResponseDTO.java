package admin.adminbackend.dto.email;


import admin.adminbackend.domain.EmailCertification;
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

    public static EmailResponseDTO change(EmailRequestDTO emailRequestDTO) {
        return new EmailResponseDTO(emailRequestDTO.getEmail());
    }

}
