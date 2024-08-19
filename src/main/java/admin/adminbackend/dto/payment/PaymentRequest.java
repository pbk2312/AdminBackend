package admin.adminbackend.dto.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Getter
@NoArgsConstructor
public class PaymentRequest {

    @NotBlank(message = "이메일을 입력해주세요")
    private String email;

    @NotBlank(message = "결제 금액")
    private String amount;

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "email='" + email + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
