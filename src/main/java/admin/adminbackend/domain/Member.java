package admin.adminbackend.domain;

import admin.adminbackend.dto.MemberDTO;
import admin.adminbackend.openapi.domain.VentureListInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    // 병합된 부분: 필드들 통합
    private String name;
    private String phoneNumber;
    private LocalDate dateOfBirth;  // 생일 필드를 LocalDate로 수정

    // Member -> MemberDTO 변환 메소드
    public MemberDTO toMemberDTO() {
        return MemberDTO.builder()
                .id(this.id)
                .email(this.email)
                .name(this.name)  // name 필드 추가
                .phoneNumber(this.phoneNumber)  // phoneNumber 필드 추가
                .dateOfBirth(this.dateOfBirth)  // 생일 추가
                .memberRole(this.memberRole != null ? this.memberRole.name() : null).build();
    }
}