package admin.adminbackend.domain.kim;

import admin.adminbackend.domain.Member;
import admin.adminbackend.investcontract.domain.InvestorInvestment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Entity
@Data
public class VentureListInfo {

    private String code; //신규_재확인코드
    private String mainProduct; //주생산품
    private String area; //지역
    private String address; //간략주소
    private String registInstitution; //벤처확인기관
    private String endDate; //벤처유효종료일
    private String registType; //벤처확인유형
    private String typeName; //업종명_10차
    private String typeName_spc; //업종분류_기보
    private String name; //업체명
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //연번
    private String owner; //대표자명
    private String startDate; //벤처유효시작일
    private String ventureNumber; //사업자 등록번호

    @JsonIgnore // 이 필드는 응답에 포함되지 않음
    private UploadFile attachFile; //첨부파일
    private String b_stt; // 사업자 상태 필드 추가

    @Enumerated(EnumType.STRING)
    private VentureApplyStatus ventureApplyStatus;

    public VentureListInfo() {
    }

    public VentureListInfo(String code, String mainProduct, String area, String address, String registInstitution, String endDate, String registType, String typeName, String typeName_spc, String name, Long id, String owner, String startDate, String ventureNumber, UploadFile attachFile, String b_stt, VentureApplyStatus ventureApplyStatus, Member member, List<InvestorInvestment> investorInvestments) {
        this.code = code;
        this.mainProduct = mainProduct;
        this.area = area;
        this.address = address;
        this.registInstitution = registInstitution;
        this.endDate = endDate;
        this.registType = registType;
        this.typeName = typeName;
        this.typeName_spc = typeName_spc;
        this.name = name;
        this.id = id;
        this.owner = owner;
        this.startDate = startDate;
        this.ventureNumber = ventureNumber;
        this.attachFile = attachFile;
        this.b_stt = b_stt;
        this.ventureApplyStatus = ventureApplyStatus;
        this.member = member;
        this.investorInvestments = investorInvestments;
    }

    //생성자 추가 (repository에서 일부 필드만 가져옴)
    public VentureListInfo(String code, String mainProduct, String area, String address, String registInstitution,
                           String endDate, String registType, String typeName, String typeName_spc,
                           String name, Long id, String owner, String startDate, String ventureNumber) {
        this.code = code;
        this.mainProduct = mainProduct;
        this.area = area;
        this.address = address;
        this.registInstitution = registInstitution;
        this.endDate = endDate;
        this.registType = registType;
        this.typeName = typeName;
        this.typeName_spc = typeName_spc;
        this.name = name;
        this.id = id;
        this.owner = owner;
        this.startDate = startDate;
        this.ventureNumber = ventureNumber;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = true)
    @JsonIgnore // 이 필드는 응답에 포함되지 않음
    private Member member;


    @OneToMany(mappedBy = "ventureListInfo")
    @JsonIgnore // 이 필드는 응답에 포함되지 않음
    private List<InvestorInvestment> investorInvestments; // List to hold investments related to this VentureListInfo


}
