package admin.adminbackend.dto.myPage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class IRNotificationDTO {

    private Long id;
    private String ventureName;
    private String ventureEmail;
    private Long personId;
    private String personEmail;
    private LocalDateTime createdAt;
}
