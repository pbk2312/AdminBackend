package admin.adminbackend.investcontract.service;

import admin.adminbackend.investcontract.domain.ContractInvestment;
import admin.adminbackend.investcontract.dto.ContractInvestmentDTO;
import admin.adminbackend.investcontract.dto.InvestorInvestmentDTO;
import admin.adminbackend.investcontract.dto.VentureInvestmentDTO;
import admin.adminbackend.investcontract.repository.ContractInvestmentRepository;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PDFGenerator {

    @Value("${file.storage.path}")
    private String storagePath;

    @Autowired
    private ContractInvestmentRepository contractInvestmentRepository;

    // 최종 계약서 생성 및 암호화
    public void generateFinalContract(Long Id, String contractId, ContractInvestmentDTO contractDTO,
                                      InvestorInvestmentDTO investorDTO, VentureInvestmentDTO ventureDTO,
                                      String ownerPassword, String userPassword) throws IOException {
        log.info("Id: {}", Id);
        log.info("contractId: {}", contractId);
        String filledFilePath = storagePath + "contract_" + contractId + ".pdf";
        String protectedFilePath = storagePath + "protected_contract_" + contractId + ".pdf";

        // 계약서 작성
        createContract(filledFilePath, contractDTO, investorDTO, ventureDTO);

        // 계약서 암호화
        encryptPdf(new File(filledFilePath), new File(protectedFilePath), ownerPassword, userPassword);

        log.info("계약서 완성본 저장 경로: " + filledFilePath);
        log.info("암호화된 계약서 완성본 저장 경로: " + protectedFilePath);

        // 계약서 상태 업데이트
        ContractInvestment contract = contractInvestmentRepository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found for ID: " + Id));
        contract.setGenerated(true); // 계약서 생성 완료 상태로 설정
        contractInvestmentRepository.save(contract);
        log.info("계약서 상태 업데이트 완료: 계약서 생성 완료 상태로 설정됨.");
    }

    // PDF 양식으로 계약서 생성
    private void createContract(String outputFilePath, ContractInvestmentDTO contractDTO,
                                InvestorInvestmentDTO investorDTO, VentureInvestmentDTO ventureDTO) throws IOException {
        //String templatePath = "D:/Admin/ProtectedFileStore/test4.pdf"; // 템플릿 경로 설정
        String templatePath = "src/main/resources/templates/test4.pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(templatePath), new PdfWriter(outputFilePath))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            if (form == null) {
                throw new IllegalArgumentException("No form found in the PDF.");
            }

            Map<String, PdfFormField> fields = form.getFormFields();
            if (fields.isEmpty()) {
                throw new IllegalArgumentException("No fields found in the PDF.");
            }

            String fontPath = "src/main/resources/templates/NanumMyeongjo.ttf"; // 폰트 경로
            PdfFont font = PdfFontFactory.createFont(fontPath, "Identity-H", true);

            log.info("투자자 DTO:{}", investorDTO.toString());
            log.info("기업 DTO:{}", ventureDTO.toString());

            // 계약서 DTO의 필드에 따라 PDF 필드에 값 설정
            Map<String, String> fieldData = new HashMap<>();
            fieldData.put("investorName1", investorDTO.getInvestorName());
            fieldData.put("ventureName1", ventureDTO.getBusinessName());
            fieldData.put("price", String.valueOf(investorDTO.getPrice()));
            fieldData.put("year", String.valueOf(LocalDate.now().getYear()));
            fieldData.put("month", String.valueOf(LocalDate.now().getMonthValue()));
            fieldData.put("date", String.valueOf(LocalDate.now().getDayOfMonth()));
            fieldData.put("investorAddress", investorDTO.getAddress());
            fieldData.put("investorBusinessName", investorDTO.getBusinessName());
            fieldData.put("investorName2", investorDTO.getInvestorName());
            fieldData.put("ventureAddress", ventureDTO.getAddress());
            fieldData.put("ventureBusinessName", ventureDTO.getBusinessName());
            fieldData.put("ventureName2", ventureDTO.getName());

            log.info("fieldData : {}", fieldData);

            // PDF 필드에 값 설정
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
            form.flattenFields(); // 필드 플래튼
        }
    }

    // 필드 매핑 설정
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
    private void encryptPdf(File inputFile, File outputFile, String ownerPassword, String userPassword) throws IOException {
        try (PDDocument document = PDDocument.load(inputFile)) {
            AccessPermission accessPermission = new AccessPermission();
            StandardProtectionPolicy protectionPolicy = new StandardProtectionPolicy(ownerPassword, userPassword, accessPermission);
            protectionPolicy.setEncryptionKeyLength(128);  // 128-bit 암호화
            protectionPolicy.setPermissions(accessPermission);

            document.protect(protectionPolicy); // 암호화 설정
            document.save(outputFile); // 암호화된 파일 저장
        }
    }
}