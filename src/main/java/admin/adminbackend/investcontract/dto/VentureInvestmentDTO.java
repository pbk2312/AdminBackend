package admin.adminbackend.investcontract.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class VentureInvestmentDTO {

    private String name; // 대표명
    private String address; // 주소
    private String businessName; // 상호명
    private Long investorId; // 투자자 (회원) ID
    private Long paymentId; // 결제 정보 ID
    private Long ventureId; // 벤처 정보 ID
    private Long ventureInvestmentId;

}
