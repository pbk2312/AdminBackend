package admin.adminbackend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member investor; // 투자자 (회원)

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment; // 결제 정보

    private Long amount; // 투자 금액

    private LocalDateTime investedAt; // 투자 시각

}
