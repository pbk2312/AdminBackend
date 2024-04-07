package admin.adminbackend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @Builder
    public Member(String email, String password, MemberRole memberRole) {
        this.email = email;
        this.password = password;
        this.memberRole = memberRole;
    }





}
