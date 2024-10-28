package admin.adminbackend.domain;

import admin.adminbackend.domain.kim.VentureListInfo;
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

    private String investmentUid; // 투자번호

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member investor; // 투자자 (회원)

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment; // 결제 정보

    private Long price; // 투자 금액

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Venture_id", nullable = false)
    private VentureListInfo ventureListInfo;

    private LocalDateTime investedAt; // 투자 시각

    private String businessName; // 상호명

    private String address;

}
