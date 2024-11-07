package admin.adminbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InvestmentHistoryDTO {


    private String investmentUid; // 투자번호



    private Long amount; // 투자 금액


    private LocalDateTime investedAt; // 투자 시각

    private String memberName; // 투자자 이름 추가

    private String ventureName; // 벤처 기업 이름 추가

    private String pending_status;

    private String paymentUid;

    private Long ventureId;

    private Long investorId;




}
