package admin.adminbackend.openapi.service;

import admin.adminbackend.domain.Member;
import admin.adminbackend.openapi.Repository.VentureListInfoRepository;
import admin.adminbackend.openapi.domain.UploadFile;
import admin.adminbackend.openapi.domain.VentureListInfo;
import admin.adminbackend.openapi.domain.VentureListInfoForm;
import admin.adminbackend.openapi.exception.ResourceNotFoundException;
import admin.adminbackend.openapi.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentureService {

    public final VentureListInfoRepository ventureListInfoRepository;
    private final FileStore fileStore;
    private final VentureStatusService ventureStatusService;

    /*public Long saveVenture(VentureListInfoForm form) throws IOException {
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());

        JSONObject ventureStatus = ventureStatusService.getCompanyNum(form.getVentureNumber());
        if (ventureStatus != null) {
            form.setB_stt((String) ventureStatus.get("b_stt"));
        }

        /*//* 1. Venture 테이블에 데이터 저장
        Venture venture = new Venture();
        venture.setVentureName(form.getVentureName());
        venture.setOwnerName(form.getOwnerName());
        venture.setVentureNumber(form.getVentureNumber());
        venture.setAttachFile(attachFile);
        venture.setB_stt(form.getB_stt());

        Venture savedVenture = ventureRepository.save(venture);*/


    // 2. VentureListInfo 테이블에 저장
        /*VentureListInfo ventureListInfo = new VentureListInfo();

        ventureListInfo.setCode(form.getCode());
        ventureListInfo.setMainProduct(form.getMainProduct());
        ventureListInfo.setArea(form.getArea());
        ventureListInfo.setAddress(form.getAddress());
        ventureListInfo.setRegistInstitution(form.getRegistInstitution());
        ventureListInfo.setEndDate(form.getEndDate());
        ventureListInfo.setRegistType(form.getRegistType());
        ventureListInfo.setTypeName(form.getTypeName());
        ventureListInfo.setTypeName_spc(form.getTypeName_spc());
        ventureListInfo.setName(form.getName());
        ventureListInfo.setOwner(form.getOwner());
        ventureListInfo.setStartDate(form.getStartDate());
        ventureListInfo.setVentureNumber(form.getVentureNumber());
        ventureListInfo.setAttachFile(attachFile);
        ventureListInfo.setB_stt(form.getB_stt());

        VentureListInfo savedVentureListInfo = ventureListInfoRepository.save(ventureListInfo);


        return savedVentureListInfo.getId();
    }*/

    public Long saveVenture(VentureListInfoForm form, MultipartFile file, Member member) throws IOException {
        // 파일을 저장하고, 저장된 파일의 정보(예: 파일 경로, 이름 등)를 얻기
        UploadFile attachFile = fileStore.storeFile(file);

        // 기업 상태를 조회
        JSONObject ventureStatus = ventureStatusService.getCompanyNum(form.getVentureNumber(),member);
        if (ventureStatus != null) {
            form.setB_stt((String) ventureStatus.get("b_stt"));
        }

        // VentureListInfo 엔티티 객체를 생성, 폼 데이터를 설정
        VentureListInfo ventureListInfo = new VentureListInfo();
        ventureListInfo.setCode(form.getCode());
        ventureListInfo.setMainProduct(form.getMainProduct());
        ventureListInfo.setArea(form.getArea());
        ventureListInfo.setAddress(form.getAddress());
        ventureListInfo.setRegistInstitution(form.getRegistInstitution());
        ventureListInfo.setEndDate(form.getEndDate());
        ventureListInfo.setRegistType(form.getRegistType());
        ventureListInfo.setTypeName(form.getTypeName());
        ventureListInfo.setTypeName_spc(form.getTypeName_spc());
        ventureListInfo.setName(form.getName());
        ventureListInfo.setOwner(form.getOwner());
        ventureListInfo.setStartDate(form.getStartDate());
        ventureListInfo.setVentureNumber(form.getVentureNumber());
        ventureListInfo.setAttachFile(attachFile); // 파일 정보를 설정
        ventureListInfo.setB_stt(form.getB_stt());

        // VentureListInfo 엔티티를 저장
        VentureListInfo savedVentureListInfo = ventureListInfoRepository.save(ventureListInfo);

        // 저장된 VentureListInfo의 ID를 반환
        return savedVentureListInfo.getId();
    }


    public VentureListInfo getVentureById(Long id) {
        return ventureListInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venture not found with id " + id));
    }

    public ResponseEntity<Resource> downloadAttach(Long id) throws MalformedURLException {
        //Venture venture = getVentureById(id);
        VentureListInfo ventureListInfo = getVentureById(id);

        String storeFileName = ventureListInfo.getAttachFile().getStoreFileName();
        String uploadFileName = ventureListInfo.getAttachFile().getUploadFileName();

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        log.info("uploadFileName={}", uploadFileName);

        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}
