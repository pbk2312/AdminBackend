package admin.adminbackend.openapi.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VentureListInfoForm {
    private String code; // 신규_재확인코드
    private String mainProduct; // 주생산품
    private String area; // 지역
    private String address; // 간략주소
    private String registInstitution; // 벤처확인기관
    private String endDate; // 벤처유효종료일
    private String registType; // 벤처확인유형
    private String typeName; // 업종명_10차
    private String typeName_spc; // 업종분류_기보
    private String name; // 업체명
    private Long id; // 연번
    private String owner; // 대표자명
    private String startDate; // 벤처유효시작일

    //private String ventureName; //기업명
    //private String ownerName; //대표자명
    private String ventureNumber; //사업자 등록번호
    private MultipartFile attachFile; //첨부파일
    //private List<MultipartFile> imageFiles;

    private String b_stt; // 사업자 상태 필드 추가


}