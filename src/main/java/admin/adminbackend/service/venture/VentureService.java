package admin.adminbackend.service.venture;

import admin.adminbackend.domain.Member;
import admin.adminbackend.domain.MemberRole;
import admin.adminbackend.repository.ventrue.VentureListInfoRepository;
import admin.adminbackend.domain.kim.UploadFile;
import admin.adminbackend.domain.kim.VentureListInfo;
import admin.adminbackend.domain.kim.VentureListInfoForm;
import admin.adminbackend.exception.ResourceNotFoundException;
import admin.adminbackend.file.FileStore;
import admin.adminbackend.repository.member.MemberRepository;
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
    private final MemberRepository memberRepository;

    public Long saveVenture(VentureListInfoForm form, MultipartFile file, Member member) throws IOException {
        // 파일을 저장하고, 저장된 파일의 정보(예: 파일 경로, 이름 등)를 얻기
        UploadFile attachFile = fileStore.storeFile(file);

        // 기업 상태를 조회
        JSONObject ventureStatus = ventureStatusService.getCompanyNum(form.getVentureNumber());
        if (ventureStatus != null) {
            form.setB_stt((String) ventureStatus.get("b_stt"));
        } else {
            form.setB_stt(null);
        }
        log.info("b_stt:{}", form.getB_stt());

        // member 기업 승급
        member.setMemberRole(MemberRole.VENTURE);
        memberRepository.save(member);

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
        ventureListInfo.setMember(member);

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