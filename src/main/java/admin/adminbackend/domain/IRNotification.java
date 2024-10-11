package admin.adminbackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
        @JoinColumn(name = "ceo_id")
        @JsonIgnore  // 순환 참조 방지
        private Member ceo; // 수령인

        private boolean isRead;

        private LocalDateTime createdAt = LocalDateTime.now();  // 기본값 설정

        private String ventureName;


        @ManyToOne
        @JoinColumn(name = "member_id")
        @JsonIgnore  // 순환 참조 방지
        private Member member; // 발송인
}
