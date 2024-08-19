package admin.adminbackend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class IRNotification {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "member_id")
        private Member member; // 수령인

        private boolean isRead;

        private LocalDateTime createdAt = LocalDateTime.now();  // 기본값 설정


        @ManyToOne
        @JoinColumn(name = "shipper_id")
        private Member shipper; // 발송인
}
