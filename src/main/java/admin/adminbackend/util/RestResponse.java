package admin.adminbackend.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
public class RestResponse<T> {

    public static <T> ResponseEntity<T> success(T body) {
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
