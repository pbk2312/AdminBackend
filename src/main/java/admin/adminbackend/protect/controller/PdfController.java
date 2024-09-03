package admin.adminbackend.protect.controller;

import admin.adminbackend.protect.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    // 파일 이름 인코딩을 위한 메서드 추가
    private static String encodeFileName(String filename) {
        try {
            return URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()).replace("+", "%20");
        } catch (Exception e) {
            return filename;
        }
    }

    @PostMapping("/protect")
    public ResponseEntity<Resource> protectPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("ownerPassword") String ownerPassword,
            @RequestParam("userPassword") String userPassword) {
        try {
            File protectedPdf = pdfService.protectPdf(file, ownerPassword, userPassword);
            Resource resource = new FileSystemResource(protectedPdf);

            // 인코딩된 파일 이름 사용
            String encodedFileName = encodeFileName(protectedPdf.getName());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(protectedPdf.length())
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download-pdf")  //서버에 저장된 특정 PDF 파일을 클라이언트가 다운로드할 수 있게 함 (나중에)
    public ResponseEntity<InputStreamResource> downloadPDF() {
        try {
            File pdfFile = new File("path/to/pdf-file.pdf");

            // 파일 존재 여부 확인
            if (!pdfFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdfFile.getName() + "\"");
            headers.setContentType(MediaType.APPLICATION_PDF);

            InputStreamResource resource = new InputStreamResource(new FileInputStream(pdfFile));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfFile.length())
                    .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}

