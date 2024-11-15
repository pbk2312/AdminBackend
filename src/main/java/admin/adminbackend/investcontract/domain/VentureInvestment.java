package admin.adminbackend.investcontract.domain;

import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.Payment;
import admin.adminbackend.domain.kim.VentureListInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VentureInvestment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //private String VentureID; // 기업 ID 추가??
    private String name;  //대표명
    private String address;  //주소
    private String businessName;  //상호명
    //private String registrationNumber; //사업자번호 (암호 설정)

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member ceo; // 대표자 (회원)

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment; // 결제 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venture_id"/* nullable = false*/)
    private VentureListInfo ventureListInfo; // 기업 정보

}

