package admin.adminbackend.openapi.Repository;

import admin.adminbackend.openapi.domain.VentureListInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VentureListInfoRepository extends JpaRepository<VentureListInfo,Long> {

    //VentureListInfo findByName(String name); //기업 정보 조회

    Optional<VentureListInfo> findById(Long id); //기업 정보 조회
    Page<VentureListInfo> findAll(Pageable pageable); // 전체 기업 목록 조회 페이징 처리


}