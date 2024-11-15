package admin.adminbackend.controller.venture;

import admin.adminbackend.domain.Member;
import admin.adminbackend.service.venture.VentureService;
import admin.adminbackend.service.member.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import admin.adminbackend.domain.kim.VentureListInfoForm;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VentureController {

    private final VentureService ventureService;
    private final MemberService memberService;

    @PostMapping(value = "/ventures/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Long>> saveVenture(
            @RequestParam("name") String name,
            @RequestParam("owner") String owner,
            @RequestParam("ventureNumber") String ventureNumber,
            @RequestParam("mainProduct") String mainProduct,
            @RequestParam("typeName") String typeName,
            @RequestPart("file") MultipartFile file,
            @RequestParam("email") String email
    ) throws IOException {
        log.info("새 벤처 저장 요청 시작");

        Member member = memberService.findByEmail(email);

        // 입력된 form 데이터와 파일의 기본 정보 로깅
        log.info("Received data: name={}, owner={}, ventureNumber={}, mainProduct={}, typeName={}", name, owner, ventureNumber, mainProduct, typeName);
        log.info("Received file: name={}, size={} bytes, contentType={}", file.getOriginalFilename(), file.getSize(), file.getContentType());

        VentureListInfoForm form = new VentureListInfoForm(mainProduct, typeName, name, owner, ventureNumber);
        Long ventureId = ventureService.saveVenture(form, file,member);

        // 벤처 ID 생성 후 로깅
        log.info("Venture saved with ID: {}", ventureId);

        Map<String, Long> response = new HashMap<>();
        response.put("ventureId", ventureId);

        log.info("응답 데이터 준비 완료: {}", response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/attach/{id}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long id) throws MalformedURLException {
        return ventureService.downloadAttach(id);
    }
}