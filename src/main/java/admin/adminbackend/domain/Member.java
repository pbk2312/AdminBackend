package admin.adminbackend.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member {


    @Id
    private String mid;
    private String mpw;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void changePw(String mpw) {
        this.mpw = mpw;
    }


}
