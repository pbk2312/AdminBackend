package admin.adminbackend.domain;

import admin.adminbackend.dto.MemberDTO;
import admin.adminbackend.domain.kim.VentureListInfo;
import admin.adminbackend.investcontract.domain.InvestorInvestment;
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
    @Column(nullable = false)
    private MemberRole memberRole;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private VentureListInfo ventureListInfo;

    @OneToMany(mappedBy = "investor", cascade = CascadeType.ALL, orphanRemoval = true) // Member가 삭제되면 투자내역도 삭제
    private List<InvestorInvestment> investorInvestments;

    // IRNotification 수령인 (ceo 역할)
    @OneToMany(mappedBy = "ceo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IRNotification> receivedIRNotifications;

    // IRNotification 발송인 (member 역할)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IRNotification> sentIRNotifications;

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