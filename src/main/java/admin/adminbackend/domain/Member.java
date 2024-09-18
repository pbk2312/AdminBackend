package admin.adminbackend.domain;

import admin.adminbackend.dto.MemberDTO;
import admin.adminbackend.openapi.domain.VentureListInfo;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private VentureListInfo ventureListInfo;

    @OneToMany(mappedBy = "investor")
    private List<Investment> investments; // 투자 내역


    // Member -> MemberDTO 변환 메소드
    public MemberDTO toMemberDTO() {
        return MemberDTO.builder()
                .id(this.id)
                .email(this.email)
                .memberRole(this.memberRole != null ? this.memberRole.name() : null).build();
    }
}
