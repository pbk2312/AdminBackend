package admin.adminbackend.openapi.service;

import admin.adminbackend.openapi.dto.OpenApiDto;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class VentureApiManager {
    private final String BASE_URL = "https://api.odcloud.kr/api";
    private final String apiUri = "/15084581/v1/uddi:fce46dc0-d056-4466-b2a3-c0222b0d6d3a";
    private final String serviceKey = "?ServiceKey=0gzS7HlAgLKTozgbo5ims+LzvqV1JFFi+f4U5RHPao936LyED6swa1o6MnbTMaA18evjCl8dOF5GKJipHIrr1g==";
    //private final String defaultQueryParam = "&MobileOS=ETC&MobileApp=AppTest&_type=json";
    private final String page = "&page=1";
    private final String perPage = "&perPage=10";
    //private final String contentTypeId = "&contentTypeId=12";


    /*public List<OpenApiDto> fetchByVenture() {
        String ventureUrl = makeUrl();
        return fetch()
    }*/

    private String makeUrl() throws UnsupportedEncodingException {
        return BASE_URL + apiUri + page + perPage + serviceKey;
    }

    /*public ResponseEntity<?> fetch() throws UnsupportedEncodingException {
        System.out.println(makeUrl());
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Map> resultMap = restTemplate.exchange(makeUrl(), HttpMethod.GET, entity, Map.class);
        System.out.println(resultMap.getBody());
        return resultMap;
    }*/

    //오픈 API 서버로부터 데이터 받아오기
    public List<OpenApiDto> fetch(String url) throws ParseException {

        List<OpenApiDto> result = new ArrayList<>();

        try {
            RestTemplate restTemplate = new RestTemplate();
            String jsonString = restTemplate.getForObject(url, String.class);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
            // 가장 큰 JSON 객체 response 가져오기
            JSONObject jsonResponse = (JSONObject) jsonObject.get("response");

            // 그 다음 body 부분 파싱
            JSONObject jsonBody = (JSONObject) jsonResponse.get("body");

            // 그 다음 위치 정보를 배열로 담은 items 파싱
            JSONObject jsonItems = (JSONObject) jsonBody.get("items");

            // items는 JSON임, 이제 그걸 또 배열로 가져온다
            JSONArray jsonItemList = (JSONArray) jsonItems.get("item");


            for (Object o : jsonItemList) {
                JSONObject item = (JSONObject) o;
                result.add(makeLocationDto(item));
            }
            return result;
        }catch (Exception e) {

        } finally {
            return result;
        }

    }

    // 콘텐츠 정보 JSON 을 DTO 로 변환
    private OpenApiDto makeLocationDto(JSONObject item) {
        // 가끔 좌표 데이터가 타입이 다른경우 처리
        if (item.get("mapx") instanceof String || item.get("mapy") instanceof String
                || item.get("addr1") == null || item.get("firstimage") == null
                || item.get("areacode") == null || item.get("contenttypeid") == null || item.get("title") == null) {
            return null;
        }
        return OpenApiDto.builder().
                /*title((String) item.get("title")).
                address((String) item.get("addr1")).
                areaCode((Long) item.get("areacode")).
                contentTypeId((Long) item.get("contenttypeid")).
                firstImage((String) item.get("firstimage")).
                mapX((double) item.get("mapx")).
                mapY((double) item.get("mapy")).*/
                build();
    }

}
