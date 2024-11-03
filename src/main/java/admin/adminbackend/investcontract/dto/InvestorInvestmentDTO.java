package admin.adminbackend.investcontract.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
public class InvestorInvestmentDTO {

    //private Long investorId;
    //private String investmentUid; // 투자번호
    //private Long id;
    private Long paymentId; // 결제 정보 ID
    private Long price; // 투자 금액
    private Long ventureId; // 벤처 기업 ID
    //private LocalDateTime investedAt; // 투자 시각

    private String address; // 주소
    private String businessName; // 상호명
    private String investorName;  //대표자명

    //private LocalDate dateOfBirth;

    private String accessToken; // 테스트 용

    private Long memberId;


}