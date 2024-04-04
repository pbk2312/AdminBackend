package admin.adminbackend.openapi.service;


import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


@RestController
@Log4j2
public class VentureApiManager {

    @GetMapping("/api")
    public ResponseEntity<String> callApi() {
        StringBuilder result = new StringBuilder();
        try {
            String urlStr = "https://api.odcloud.kr/api/15084581/v1/uddi:fce46dc0-d056-4466-b2a3-c0222b0d6d3a?" +
                    "page=1" + "&perPage=10" + "&serviceKey=0gzS7HlAgLKTozgbo5ims%2BLzvqV1JFFi%2Bf4U5RHPao936LyED6swa1o6MnbTMaA18evjCl8dOF5GKJipHIrr1g%3D%3D";
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String returnLine;
            while((returnLine = br.readLine()) != null) {
                result.append(returnLine).append("\n");
            }
            urlConnection.disconnect();
        } catch (IOException e) {
            log.error("API 호출 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("API 호출 중 오류 발생: " + e.getMessage());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new ResponseEntity<>(result.toString(), headers, HttpStatus.OK);
    }
}
