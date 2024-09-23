package admin.adminbackend;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Log4j2
public class RedisService {


    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper; // ObjectMapper 주입

    public void setStringValue(String memberId, String token, Long expirationTime) {
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        stringValueOperations.set(memberId, token, expirationTime, TimeUnit.MILLISECONDS);
    }

    public void deleteStringValue(String memberId) {
        stringRedisTemplate.delete(memberId);
        log.info("Redis에서 키 삭제 완료: {}", memberId);
    }


    // refreshToken으로 memberId 찾기
    public String findMemberIdByRefreshToken(String refreshToken) {
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        // 모든 키를 순회하면서 refreshToken과 일치하는 값을 찾는다
        for (String key : stringRedisTemplate.keys("*")) {
            String storedToken = stringValueOperations.get(key);
            if (refreshToken.equals(storedToken)) {
                log.info("Redis에서 refreshToken으로 memberId 찾기 완료: {}", key);
                return key; // memberId 반환
            }
        }
        return null;
    }


}
