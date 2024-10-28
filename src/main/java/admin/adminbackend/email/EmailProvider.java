package admin.adminbackend.email;

import admin.adminbackend.dto.email.EmailRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender javaMailSender;

    private final String SUBJECT = "[스타트업 투자 플랫폼]"; // 인증 메일 제목

    public boolean sendCertificationMail(String email, String certificationNumber) {

        try {

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            String htmlContent = getCertificationMessage(certificationNumber);

            messageHelper.setTo(email);
            messageHelper.setSubject(SUBJECT);
            messageHelper.setText(htmlContent, true);

            javaMailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;


    }

    public boolean sendPasswordResetEmail(EmailRequestDTO emailRequestDTO, String resetLink) {
        if (emailRequestDTO == null || emailRequestDTO.getEmail() == null || emailRequestDTO.getEmail().isEmpty()) {
            // 이메일 주소가 유효하지 않으면 이메일을 보내지 않고 false를 반환합니다.
            return false;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            String emailBody = getEmailBody(resetLink);

            messageHelper.setTo(emailRequestDTO.getEmail());
            messageHelper.setSubject(SUBJECT);
            messageHelper.setText(emailBody, true);

            javaMailSender.send(message);
            return true;
        } catch (MessagingException e) {
            // 이메일 보내기 작업에서 예외가 발생하면 실패한 이유를 로그에 기록하고 false를 반환합니다.
            e.printStackTrace();
            return false;
        }
    }


    private String getEmailBody(String resetLink) {
        return "안녕하세요,\n\n비밀번호를 재설정하려면 아래 링크를 클릭하세요:\n" + resetLink;
    }

    private String getCertificationMessage(String certificationNumber) {
        String certificationMessage = "";
        certificationMessage += "<h1 style='text-align: center;'> [스타트업 투자 플랫폼] 인증메일</h1>";
        certificationMessage += "<h3 style='text-align: center;'> 인증코드 : <strong style='font-size:32px;letter-spacing:8px;'>" + certificationNumber + "</strong></h3>";
        return certificationMessage;

    }

    public boolean sendFileEmail(String to, String subject, String text, MultipartFile file) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(text, true);

            if (file != null && !file.isEmpty()) {
                messageHelper.addAttachment(file.getOriginalFilename(), file);
            }

            javaMailSender.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }


    }
    public String createEmailSubject() {
        return "[스타트업 투자 플랫폼] IR 자료";
    }

    public String createEmailBody(Long ventureId) {
        String readUrl = "http://localhost:8080/invest?ventureId=" + ventureId;
        return "안녕하세요,<br><br>첨부된 파일을 확인해 주세요.<br>" +
                "IR 자료를 읽으시고 투지를 진행하실거면 해당 링크를 클릭해주세요: <a href=\"" + readUrl + "\">" + readUrl + "</a>";
    }

}
