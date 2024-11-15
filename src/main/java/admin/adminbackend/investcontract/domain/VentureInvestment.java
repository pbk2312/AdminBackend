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
    private String name;  //대표명
    private String address;  //주소
    private String businessName;  //상호명

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member ceo; // 대표자 (회원)

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment; // 결제 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venture_id")
    private VentureListInfo ventureListInfo; // 기업 정보

}

