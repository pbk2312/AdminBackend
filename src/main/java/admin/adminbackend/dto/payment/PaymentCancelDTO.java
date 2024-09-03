package admin.adminbackend.dto.payment;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCancelDTO {

    /**
     * 결제 건의 주문 번호 (paymentUid UID)
     */
    private Long investmentId;
    private String paymentUid;

    /**
     * 환불 금액
     */
    private Long cancelRequestAmount;

    /**
     * 환불 사유
     */
    private String reason;


}
