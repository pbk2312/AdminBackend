package admin.adminbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InvestmentDTO {

    private Long investmentId;
    private String investmentUid; // 투자번호
    private Long paymentId; // 결제 정보 ID
    private Long amount; // 투자 금액
    private Long ventureId; // 벤처 기업 ID
    private LocalDateTime investedAt; // 투자 시각

    private String address; // 주소
    private String businessName; // 상호명
    private String investorName;

    private String accessToken; // 테스트 용


}
