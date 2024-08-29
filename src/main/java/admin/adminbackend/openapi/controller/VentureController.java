package admin.adminbackend.openapi.controller;

import admin.adminbackend.openapi.domain.VentureListInfo;
import admin.adminbackend.openapi.service.VentureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import admin.adminbackend.openapi.dto.VentureListInfoForm;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VentureController {

    private final VentureService ventureService;

    /*@PostMapping("/ventures/new")
    public ResponseEntity<Long> saveVenture(@RequestBody VentureListInfoForm form) throws IOException {
        Long ventureId = ventureService.saveVenture(form);
        return ResponseEntity.ok(ventureId);
    }*/

    @PostMapping(value = "/ventures/new", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, Long>> saveVenture(
            @RequestPart("form") VentureListInfoForm form,
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        Long ventureId = ventureService.saveVenture(form, file);
        Map<String, Long> response = new HashMap<>();
        response.put("ventureId", ventureId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ventures/{id}")
    public ResponseEntity<VentureListInfo> ventures(@PathVariable Long id) {
        VentureListInfo ventureListInfo = ventureService.getVentureById(id);
        return ResponseEntity.ok(ventureListInfo);
    }

    @GetMapping("/attach/{id}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long id) throws MalformedURLException {
        return ventureService.downloadAttach(id);
    }
}