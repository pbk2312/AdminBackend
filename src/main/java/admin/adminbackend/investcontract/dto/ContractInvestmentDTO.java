package admin.adminbackend.investcontract.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractInvestmentDTO {

    private Long id;
    private String contractId; // 계약서 ID
    private Long investorInvestmentId; // 투자자 투자 정보 ID
    private Long ventureInvestmentId; // 벤처 투자 정보 ID

    // 추가된 필드
    private InvestorInvestmentDTO investorInvestmentDTO; // 투자자 DTO
    private VentureInvestmentDTO ventureInvestmentDTO;   // 벤처 DTO
}
