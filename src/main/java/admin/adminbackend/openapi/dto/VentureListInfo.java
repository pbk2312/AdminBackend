package admin.adminbackend.openapi.dto;

import admin.adminbackend.domain.Investment;
import admin.adminbackend.domain.Member;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class VentureListInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //연번

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
    private String owner; //대표자명
    private String startDate; //벤처유효시작일

    @OneToOne
    @JoinColumn(name = "member_id", nullable = true)
    private Member member;


    @OneToMany(mappedBy = "ventureListInfo")
    private List<Investment> investments; // List to hold investments related to this VentureListInfo


    public VentureListInfo() {}

    public VentureListInfo(String code, String mainProduct, String area,
                           String address, String registInstitution, String endDate,
                           String registType, String typeName, String typeName_spc, String name,
                           Long id, String owner, String startDate) {
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
    }
}
