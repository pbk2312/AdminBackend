package admin.adminbackend.dto.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Builder
@Getter
@Setter
public class PaymentDTO {

    private String investmentUid; // 예약 고유 번호
    private Long memberId; // 회원 ID
    private String ventureName; // 투자하는 회사명
    private String memberEmail; // 결제자 이메일
    private Long totalPrice; // 결제 금액


}
