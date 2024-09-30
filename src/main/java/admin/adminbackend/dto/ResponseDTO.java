package admin.adminbackend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDTO<T> {
    private String message; // 응답 메시지
    private T data;         // 응답 데이터 (제네릭 타입으로 다양한 데이터 유형을 처리 가능)
}