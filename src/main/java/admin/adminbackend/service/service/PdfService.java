package admin.adminbackend.service.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class PdfService {

    @Value("${file.storage.path}")
    private String storagePath;

    public File protectPdf(MultipartFile file, String ownerPassword, String userPassword) throws IOException {

        // 업로드된 파일을 원하는 폴더에 저장
        File tempFile = new File(storagePath, "uploaded-" + file.getOriginalFilename());
        file.transferTo(tempFile);

        // 출력할 파일 위치 지정
        File outputFile = new File(storagePath, "protected-" + file.getOriginalFilename());

        // 암호화 처리
        addPasswordToPdf(tempFile, outputFile, ownerPassword, userPassword);

        System.out.println("Temporary file created at: " + tempFile.getAbsolutePath());
        System.out.println("Protected file created at: " + outputFile.getAbsolutePath());

        // 임시 파일 삭제
        //tempFile.delete();

        // 암호화된 파일 반환
        return outputFile;

    }

    public static void addPasswordToPdf(File inputFile, File outputFile, String ownerPassword, String userPassword) throws IOException {
        try (PDDocument document = PDDocument.load(inputFile)) {
            AccessPermission accessPermission = new AccessPermission();
            StandardProtectionPolicy protectionPolicy = new StandardProtectionPolicy(ownerPassword, userPassword, accessPermission);
            protectionPolicy.setEncryptionKeyLength(128); // 암호화 키 길이 설정
            protectionPolicy.setPermissions(accessPermission);

            document.protect(protectionPolicy);
            document.save(outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

