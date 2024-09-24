package admin.adminbackend.openapi.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class VentureListInfoForm {
 @NotNull
 private String code; // 신규_재확인코드
 private String mainProduct; // 주생산품 ㅇ
 @NotNull
 private String area; // 지역
 @NotNull
 private String address; // 간략주소
 @NotNull
 private String registInstitution; // 벤처확인기관
 @NotNull
 private String endDate; // 벤처유효종료일
 @NotNull
 private String registType; // 벤처확인유형
 private String typeName; // 업종명_10차 ㅇ
 @NotNull
 private String typeName_spc; // 업종분류_기보
 private String name; // 업체명 ㅇ
 @NotNull
 private Long id; // 연번
 private String owner; // 대표자명 ㅇ
 @NotNull
 private String startDate; // 벤처유효시작일

 //private String ventureName; //기업명
 //private String ownerName; //대표자명
 private String ventureNumber; //사업자 등록번호 ㅇ
 //private MultipartFile attachFile; //첨부파일 ㅇ
 //private List<MultipartFile> imageFiles;

 @NotNull
 private String b_stt; // 사업자 상태 필드 추가

 /*public VentureListInfoForm(String mainProduct, String typeName, String owner, String name, String ventureNumber) {
  this.mainProduct = mainProduct;
  this.typeName = typeName;
  this.owner = owner;
  this.name = name;
  this.ventureNumber = ventureNumber;
 }*/

 public VentureListInfoForm(String mainProduct, String typeName, String name, String owner, String ventureNumber) {
  this.mainProduct = mainProduct;
  this.typeName = typeName;
  this.name = name;
  this.owner = owner;
  this.ventureNumber = ventureNumber;
 }
}