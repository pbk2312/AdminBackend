package admin.adminbackend.openapi.UtilService;

import admin.adminbackend.openapi.dto.VentureListInfoRepository;
import admin.adminbackend.openapi.service.VentureListApiManager;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class StartupRunner {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private VentureListInfoRepository ventureListInfoRepository;

    @Autowired
    private VentureListApiManager ventureListApiManager;

    @Bean
    public ApplicationRunner initializeData() {
        return args -> {
            long count = ventureListInfoRepository.count();
            if (count == 0) {
                int totalPages = 10;  // 예를 들어 40548개의 데이터를 페이지당 5000개씩 나눈 경우
                for (int page = 1; page <= totalPages; page++) {
                    JSONArray response = ventureListApiManager.callApi(page);
                    // 로그 또는 진행 상황을 출력
                    System.out.println("Page " + page + " processed.");
                }
            }
        };
    }
}