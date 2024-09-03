package admin.adminbackend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class ResetToken {


    @Id
    @NotBlank
    @Email
    private String email;

    private String resetToken;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expiryDate;

    public ResetToken(String email, String resetToken, LocalDateTime expiryDate) {
        this.email = email;
        this.resetToken = resetToken;
        this.expiryDate = expiryDate;
    }
}
