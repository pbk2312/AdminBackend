package admin.adminbackend.openapi.service;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class VentureApiManager {

    @GetMapping("/api")
    public String callApi() throws IOException {
        StringBuilder result = new StringBuilder();

            String urlStr = "https://api.odcloud.kr/api/15084581/v1/uddi:fce46dc0-d056-4466-b2a3-c0222b0d6d3a?" +
                "&page=1" + "&perPage=10" + "&serviceKey=0gzS7HlAgLKTozgbo5ims+LzvqV1JFFi+f4U5RHPao936LyED6swa1o6MnbTMaA18evjCl8dOF5GKJipHIrr1g==";
            URL url = new URL(urlStr);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader br;

            br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF=8"));

            String returnLine;

            while((returnLine = br.readLine()) != null) {
            result.append(returnLine + "\n\r");
            }

            urlConnection.disconnect();

        return result.toString();
    }
}
