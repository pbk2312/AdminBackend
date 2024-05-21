package admin.adminbackend.openapi.service;

import admin.adminbackend.openapi.dto.VentureListInfo;
import admin.adminbackend.openapi.dto.VentureListInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentureListInfoService {

    @Autowired
    private VentureListInfoRepository ventureListInfoRepository;

    public VentureListInfo getCompanyByName(String name) {
        return ventureListInfoRepository.findByName(name);
    }

    public List<VentureListInfo> getAllCompanies() {
        return ventureListInfoRepository.findAll();
    }

    public VentureListInfo saveCompany(VentureListInfo ventureListInfo) {
        return ventureListInfoRepository.save(ventureListInfo);
    }

    public void deleteCompany(Long id) {
        ventureListInfoRepository.deleteById(id);
    }

    // 추가적인 비즈니스 로직을 여기에 작성할 수 있습니다.
}
