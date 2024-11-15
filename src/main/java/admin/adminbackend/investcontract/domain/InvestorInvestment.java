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
public class InvestorInvestment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long totalPrice; // 투자 금액
    private String investmentUid; // 투자번호
    private LocalDateTime investedAt; // 투자 시각
    private String businessName; // 상호명
    private String address;
    private String investorName; //대표자명

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member investor; // 투자자 (회원)

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment; // 결제 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venture_id")
    private VentureListInfo ventureListInfo;  //기업 정보

}