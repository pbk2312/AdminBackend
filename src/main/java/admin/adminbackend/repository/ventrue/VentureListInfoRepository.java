package admin.adminbackend.repository.ventrue;

import admin.adminbackend.domain.kim.VentureListInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface VentureListInfoRepository extends JpaRepository<VentureListInfo,Long> {

    //VentureListInfo findByName(String name); //기업 정보 조회

    Optional<VentureListInfo>   findById(Long id); //기업 정보 조회

    @Query("SELECT new admin.adminbackend.domain.kim.VentureListInfo(v.code, v.mainProduct, v.area, v.address, v.registInstitution, " +
            "v.endDate, v.registType, v.typeName, v.typeName_spc, v.name, v.id, v.owner, v.startDate, v.ventureNumber) " +
            "FROM VentureListInfo v WHERE v.id = :id")
    Optional<VentureListInfo> findPartialInfoById(Long id); //추가

    @Query("SELECT new admin.adminbackend.domain.kim.VentureListInfo(v.code, v.mainProduct, v.area, v.address, v.registInstitution, " +
            "v.endDate, v.registType, v.typeName, v.typeName_spc, v.name, v.id, v.owner, v.startDate, v.ventureNumber) " +
            "FROM VentureListInfo v")
    Page<VentureListInfo> findAll(Pageable pageable); // 전체 기업 목록 조회 페이징 처리


}
