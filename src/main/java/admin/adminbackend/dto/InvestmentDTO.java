package admin.adminbackend.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InvestmentDTO {

    private Long id;
    private String investmentUid; // 투자번호
    private Long memberId; // 투자자 ID
    private Long paymentId; // 결제 정보 ID
    private Long amount; // 투자 금액
    private Long ventureId; // 벤처 기업 ID
    private LocalDateTime investedAt; // 투자 시각

    // 필요시 추가 생성자, 메소드
}