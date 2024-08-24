package admin.adminbackend.config;

import com.siot.IamportRestClient.IamportClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Log4j2
public class AppConfig {  //0501 추가

    @Value("${imp_key}")
    private String apiKey;

    @Value("${imp_secret}")
    private String secretKey;


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    public IamportClient iamportClient() {
        log.info("API Key: {}", apiKey);
        log.info("Secret Key: {}", secretKey);
        return new IamportClient(apiKey, secretKey);
    }
}