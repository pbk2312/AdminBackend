package admin.adminbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelDTO {

    /**
     * 결제 건의 주문 번호 (paymentUid UID)
     */
    private Long reservationId;
    private String paymentUid;

    /**
     * 환불 금액
     */
    private Long cancelRequestAmount;

    /**
     * 환불 사유
     */
    private String reson;


}
