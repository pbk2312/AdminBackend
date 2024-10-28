package admin.adminbackend.domain.kim;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VentureForm {
    //private Long id;
    private String ventureName; //기업명
    private String ownerName; //대표자명
    private String ventureNumber; //사업자 등록번호
    private MultipartFile attachFile; //첨부파일
    //private List<MultipartFile> imageFiles;

    private String b_stt; // 사업자 상태 필드 추가
}
