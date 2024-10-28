package admin.adminbackend.service.contract;

import admin.adminbackend.domain.Investment;
import admin.adminbackend.domain.Member;
import admin.adminbackend.service.investment.InvestmentService;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractService {

    @Value("${file.storage.path}")
    private String storagePath;

    private Map<String, String> draft;  // 초안을 저장할 임시 저장소

    private final InvestmentService investmentService;

    // 투자자 정보 저장 (초안 생성)
    public void saveDraft(Map<String, String> investorData, Long ventureId, Member member) throws IOException {
        draft = new HashMap<>();

        // 현재 날짜를 자동으로 설정 (날짜가 없는 경우)
        LocalDate currentDate = LocalDate.now();
        investorData.putIfAbsent("year", String.valueOf(currentDate.getYear()));
        investorData.putIfAbsent("month", String.format("%02d", currentDate.getMonthValue()));
        investorData.putIfAbsent("date", String.format("%02d", currentDate.getDayOfMonth()));

        // 초안 데이터를 저장
        draft.putAll(investorData);

        log.info("Received investor data: {}", investorData);  // 투자자 정보 로그 출력

        // PDF 초안 생성
        createContractDraft(investorData);
        String price = investorData.get("price");
    }

    // 초안 PDF 생성 로직
    private void createContractDraft(Map<String, String> investorData) throws IOException {
        String templatePath = storagePath + "test4.pdf";
        String draftFilePath = storagePath + "draft_contract.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(templatePath), new PdfWriter(draftFilePath))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

            // PDF 필드 매핑 및 투자자 데이터 채우기
            Map<String, PdfFormField> fields = form.getFormFields();
            if (fields.isEmpty()) {
                throw new IllegalArgumentException("No fields found in the PDF.");
            }
            //fillPdfFields(fields, investorData);

            String fontPath = "D:/NanumMyeongjo.ttf";
            PdfFont font = PdfFontFactory.createFont(fontPath, "Identity-H", true);

            Map<String, String> fieldMapping = createFieldMappingInvestor();
            for (Map.Entry<String, String> entry : investorData.entrySet()) {
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
            form.flattenFields(); // 필드를 고정
            System.out.println("Draft contract path: " + draftFilePath);
        }
    }

    private Map<String, String> createFieldMappingInvestor() {
        Map<String, String> fieldMapping = new HashMap<>();

        fieldMapping.put("investorName1", "dhFormfield-5193825025");
        //fieldMapping.put("ventureName1", "dhFormfield-5193704257");
        fieldMapping.put("price", "dhFormfield-5193831145");
        fieldMapping.put("year", "dhFormfield-5193832166");
        fieldMapping.put("month", "dhFormfield-5193832173");
        fieldMapping.put("date", "dhFormfield-5193832174");
        fieldMapping.put("investorAddress", "dhFormfield-5193832179");
        fieldMapping.put("investorBusinessName", "dhFormfield-5193832180");
        fieldMapping.put("investorName2", "dhFormfield-5193834284");
        //fieldMapping.put("ventureAddress", "dhFormfield-5193834335");
        //fieldMapping.put("ventureBusinessName", "dhFormfield-5193834717");
        //fieldMapping.put("ventureName2", "dhFormfield-5193834787");
        return fieldMapping;
    }


    // 기업 정보 입력 후 계약서 완성
    public void completeContract(Map<String, String> companyData, String userPassword, String ownerPassword)
            throws IOException {
        log.info("Received company data: {}", companyData);  // 회사 정보 로그 출력
        log.info("Draft before adding company data: {}", draft);  // 초안 데이터 로그 출력

        // 초안에 기업 정보를 추가하여 최종 계약서 완성
        draft.putAll(companyData);

        // 최종 계약서 생성 및 암호화
        //String userPassword = draft.get("investorBirthdate").replace("-", ""); // 생년월일을 패스워드로 사용
        //String ownerPassword = companyData.get("ownerPassword");

        // 최종 계약서 생성 및 암호화
        log.info("Final draft with company data: {}", draft);  // 최종 완성된 초안 로그 출력
        log.info("User Password: {}", userPassword);  // 투자자 패스워드 로그 출력
        log.info("Owner Password: {}", ownerPassword);  // 기업 패스워드 로그 출력

        generateFinalContract(draft, ownerPassword, userPassword);
    }

    // 최종 계약서 생성 및 암호화
    private void generateFinalContract(Map<String, String> fieldData, String ownerPassword, String userPassword)
            throws IOException {
        String filledFilePath = storagePath + "final_contract.pdf";
        String protectedFilePath = storagePath + "protected_contract.pdf";

        // 계약서 작성
        createContract(filledFilePath, fieldData);

        // 계약서 암호화
        encryptPdf(new File(filledFilePath), new File(protectedFilePath), ownerPassword, userPassword);

        System.out.println("Filled contract path: " + filledFilePath);
        System.out.println("Protected contract path: " + protectedFilePath);
    }

    private void createContract(String outputFilePath, Map<String, String> fieldData) throws IOException {
        ///String templatePath = "D:/Admin/ProtectedFileStore/test4.pdf";
        String templatePath = "templates/test4.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(templatePath), new PdfWriter(outputFilePath))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            if (form == null) {
                throw new IllegalArgumentException("No form found in the PDF.");
            }

            Map<String, PdfFormField> fields = form.getFormFields();
            if (fields.isEmpty()) {
                throw new IllegalArgumentException("No fields found in the PDF.");
            }

            //String fontPath = "D:/NanumMyeongjo.ttf";
            String fontPath = "templates/NanumMyeongjo.ttf";
            PdfFont font = PdfFontFactory.createFont(fontPath, "Identity-H", true);

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

    // PDF 파일 암호화
    private void encryptPdf(File inputFile, File outputFile, String ownerPassword, String userPassword)
            throws IOException {
        try (PDDocument document = PDDocument.load(inputFile)) {
            AccessPermission accessPermission = new AccessPermission();
            StandardProtectionPolicy protectionPolicy = new StandardProtectionPolicy(ownerPassword, userPassword,
                    accessPermission);
            protectionPolicy.setEncryptionKeyLength(128);  // 128-bit 암호화
            protectionPolicy.setPermissions(accessPermission);

            document.protect(protectionPolicy); // 암호화 설정
            document.save(outputFile); // 암호화된 파일 저장
        }
    }
}
