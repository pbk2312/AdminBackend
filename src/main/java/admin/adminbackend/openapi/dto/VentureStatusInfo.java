package admin.adminbackend.openapi.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

//@Entity
@Data
public class VentureStatusInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String b_no;
    private String b_stt;
    private String b_stt_cd;
    private String tax_type;
    private String tax_type_cd;
    private String end_dt;
    private String utcc_yn;
    private String tax_type_change_dt;
    private String invoice_apply_dt;
    private String rbf_tax_type;
    private String rbf_tax_type_cd;

    public VentureStatusInfo() {
    }

    public VentureStatusInfo(String b_no, String b_stt, String b_stt_cd, String tax_type,
                             String tax_type_cd, String end_dt, String utcc_yn,
                             String tax_type_change_dt, String invoice_apply_dt,
                             String rbf_tax_type, String rbf_tax_type_cd) {
        this.b_no = b_no; //사업자등록번호
        this.b_stt = b_stt; //납세자상태(명칭)
        this.b_stt_cd = b_stt_cd; //납세자상태(코드)
        this.tax_type = tax_type; //과세유형메세지(명칭)
        this.tax_type_cd = tax_type_cd; //과세유형메세지(코드)
        this.end_dt = end_dt; //폐업일
        this.utcc_yn = utcc_yn; //단위과세전환폐업여부
        this.tax_type_change_dt = tax_type_change_dt; //최근과세유형전환일자
        this.invoice_apply_dt = invoice_apply_dt; //세금계산서적용일자
        this.rbf_tax_type = rbf_tax_type; //직전과세유형메세지(명칭)
        this.rbf_tax_type_cd = rbf_tax_type_cd; //직전과세유형메세지(코드)
    }
}
