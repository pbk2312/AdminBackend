package admin.adminbackend.dto.myPage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class IRNotificationDTO {

    private Long id;
    private Long ventureId;
    private String ventureEmail;
    private Long personId;
    private String personEmail;
    private boolean isRead;
    private LocalDateTime createdAt;
}
