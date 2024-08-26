package admin.adminbackend.openapi.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class UploadFile {
    private String uploadFileName; //업로드한 파일명
    private String storeFileName; //시스템에 저장한 파일명
    private String filePath;        // 서버에 저장된 파일 경로 11:08
    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }

    public UploadFile() {

    }
}
