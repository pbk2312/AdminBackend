package admin.adminbackend.openapi.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Venture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ventureName; //기업명
    private String ownerName; //대표자명
    private String ventureNumber; //사업자 등록번호

    @Embedded
    private UploadFile attachFile; //첨부파일 , 안에 filepath 포함되어 있음
    //private List<UploadFile> imageFiles;

    private String b_stt; // 사업자 상태 필드 추가
}
