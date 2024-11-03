package admin.adminbackend.investcontract.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ContractInvestment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contractId; // 계약서 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_investment_id", nullable = false)
    private InvestorInvestment investorInvestment; // 투자자 투자 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venture_investment_id", nullable = false)
    private VentureInvestment ventureInvestment; // 벤처 투자 정보

}