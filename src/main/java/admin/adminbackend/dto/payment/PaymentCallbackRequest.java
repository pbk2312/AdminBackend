package admin.adminbackend.dto.payment;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentCallbackRequest {


    private String paymentUid; // 결제 고유 번호
    private String investmentUid; // 투자 고유 번호
}
