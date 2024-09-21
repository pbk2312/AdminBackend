package admin.adminbackend.contract.service;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ContractService {

    @Value("${file.storage.path}")
    private String storagePath;

    public String[] generateAndProtectContract(Map<String, String> fieldData, String ownerPassword, String userPassword) throws IOException {
        String filledFilePath = storagePath + "filled_contract.pdf";
        String protectedFilePath = storagePath + "protected_filled_contract.pdf";

        // 계약서 생성
        createContract(filledFilePath, fieldData);

        // 계약서 암호화
        File filledFile = new File(filledFilePath);
        File protectedFile = new File(protectedFilePath);
        encryptPdf(filledFile, protectedFile, ownerPassword, userPassword);

        // 임시 파일 삭제 (원하면 사용)
        // filledFile.delete();

        System.out.println("Filled contract path: " + filledFilePath);
        System.out.println("Protected contract path: " + protectedFilePath);

        return new String[]{filledFilePath, protectedFilePath};
    }

    private void createContract(String outputFilePath, Map<String, String> fieldData) throws IOException {
        String templatePath = "D:/Admin/ProtectedFileStore/test4.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(templatePath), new PdfWriter(outputFilePath))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            if (form == null) {
                throw new IllegalArgumentException("No form found in the PDF.");
            }

            Map<String, PdfFormField> fields = form.getFormFields();
            if (fields.isEmpty()) {
                throw new IllegalArgumentException("No fields found in the PDF.");
            }

            // 폰트 파일 경로 (나눔명조 폰트 사용)
            String fontPath = "D:/NanumMyeongjo.ttf";
            PdfFont font = PdfFontFactory.createFont(fontPath, "Identity-H", true);

            // JSON 필드 데이터에 대해 처리
            Map<String, String> fieldMapping = createFieldMapping();
            for (Map.Entry<String, String> entry : fieldData.entrySet()) {
                String jsonKey = entry.getKey();
                String fieldValue = entry.getValue();

                String pdfFieldName = fieldMapping.get(jsonKey);
                if (pdfFieldName != null) {
                    PdfFormField pdfField = fields.get(pdfFieldName);
                    if (pdfField != null) {
                        pdfField.setValue(fieldValue)
                                .setFont(font)
                                .setFontSize(8);
                    }
                }
            }
            form.flattenFields();
        }
    }

    private Map<String, String> createFieldMapping() {
        Map<String, String> fieldMapping = new HashMap<>();
        fieldMapping.put("investorName1", "dhFormfield-5193825025");
        fieldMapping.put("ventureName1", "dhFormfield-5193704257");
        fieldMapping.put("price", "dhFormfield-5193831145");
        fieldMapping.put("year", "dhFormfield-5193832166");
        fieldMapping.put("month", "dhFormfield-5193832173");
        fieldMapping.put("date", "dhFormfield-5193832174");
        fieldMapping.put("investorAddress", "dhFormfield-5193832179");
        fieldMapping.put("investorBusinessName", "dhFormfield-5193832180");
        fieldMapping.put("investorName2", "dhFormfield-5193834284");
        fieldMapping.put("ventureAddress", "dhFormfield-5193834335");
        fieldMapping.put("ventureBusinessName", "dhFormfield-5193834717");
        fieldMapping.put("ventureName2", "dhFormfield-5193834787");
        return fieldMapping;
    }

    private void encryptPdf(File inputFile, File outputFile, String ownerPassword, String userPassword) throws IOException {
        try (PDDocument document = PDDocument.load(inputFile)) {
            AccessPermission accessPermission = new AccessPermission();
            StandardProtectionPolicy protectionPolicy = new StandardProtectionPolicy(ownerPassword, userPassword, accessPermission);
            protectionPolicy.setEncryptionKeyLength(128);
            protectionPolicy.setPermissions(accessPermission);

            document.protect(protectionPolicy);
            document.save(outputFile);
        }
    }
}
