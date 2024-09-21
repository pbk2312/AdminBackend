package admin.adminbackend.contract.controller;

import admin.adminbackend.contract.service.ContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class ContractController {

    @Autowired
    private ContractService contractService;

    @PostMapping("/generate-contract")
    public ResponseEntity<byte[]> generateContract(@RequestBody Map<String, String> fieldData) {
        log.info("Received request to generate and encrypt contract with data: {}", fieldData);

        try {
            // ownerPassword와 userPassword를 fieldData에서 추출
            String ownerPassword = fieldData.get("ownerPassword");
            String userPassword = fieldData.get("userPassword");

            // ownerPassword와 userPassword가 없는 경우 처리
            if (ownerPassword == null || userPassword == null) {
                log.error("Missing ownerPassword or userPassword in request data");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // 계약서 생성 및 암호화된 파일 경로 가져오기
            String[] filePaths = contractService.generateAndProtectContract(fieldData, ownerPassword, userPassword);
            String protectedFilePath = filePaths[1]; // 암호화된 파일 경로 (protected_contract.pdf)
            log.info("Encrypted contract generated and saved to: {}", protectedFilePath);

            // 생성된 암호화된 파일을 읽기
            File file = new File(protectedFilePath);

            // 파일이 존재하는지 확인하는 코드 추가
            if (!file.exists()) {
                log.error("File does not exist: " + protectedFilePath);
                throw new IOException("File not found at path: " + protectedFilePath);
            }

            // 파일 크기 확인
            //log.info("File size: " + file.length() + " bytes");

            byte[] pdfContent = Files.readAllBytes(file.toPath());

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "encrypted_contract.pdf");

            // PDF 파일을 바이트 배열로 클라이언트에게 반환
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error occurred while generating and encrypting contract with data: {}", fieldData, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

