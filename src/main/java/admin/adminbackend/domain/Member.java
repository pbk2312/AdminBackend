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

    private String name;
    private String nickname;  // 닉네임 필드 추가
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;  // 생일 필드를 LocalDate로 수정

    // Member -> MemberDTO 변환 메소드
    public MemberDTO toMemberDTO() {
        return MemberDTO.builder()
                .email(this.email)
                .name(this.name)  // name 필드 추가
                .nickname(this.nickname)  // nickname 필드 추가
                .phoneNumber(this.phoneNumber)  // phoneNumber 필드 추가
                .address(this.address)  // address 필드 추가
                .dateOfBirth(this.dateOfBirth)  // 생일 추가
                .memberRole(this.memberRole != null ? this.memberRole.name() : null).build();
    }

}
