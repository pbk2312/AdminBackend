package admin.adminbackend.openapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OpenApiDto {

    private Long currentCount;
    private String address;
    private String owner;
    private Long startDate;
    private Long endDate;
    private String registInstitution;
    private String registType;
    private String code;
    private String typeName; //업종명
    private String typeName_spc; //업종분류
    private String name;
    private Long id; //연번
    private String mainThing; //주생산품
    private String area;
    private Long matchCount;
    private Long page;
    private Long perPage;
    private Long totalCount;

}
