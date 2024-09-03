package admin.adminbackend.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long price; // 투자 결제 금액

    @Enumerated(EnumType.STRING) // 열거형을 문자열로 저장
    private PaymentStatus status;

    private String paymentUid; // 결제 고유 번호

    @Builder
    public Payment(Long price, PaymentStatus status) {
        this.price = price;
        this.status = status;
    }

    public void changePaymentBySuccess(PaymentStatus status, String paymentUid) {
        this.status = status;
        this.paymentUid = paymentUid;
    }


    @OneToMany(mappedBy = "payment")
    private List<Investment> investments; // 투자 내역


}
