package admin.adminbackend.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member {

    @Id
    @NotEmpty
    private String name;

    @NotEmpty
    private String password;

    @NotEmpty
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void changePw(String password) {
        this.password = password;
    }

    public void changeEmail(String email) {
        this.email = email;
    }
}
