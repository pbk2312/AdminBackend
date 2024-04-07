package admin.adminbackend.openapi.service;

import admin.adminbackend.openapi.dto.VentureInfo;
import admin.adminbackend.openapi.dto.VentureInfoReository;
import lombok.extern.log4j.Log4j2;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//import net.minidev.json.JSONArray;
//import net.minidev.json.JSONObject;
//import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@RestController
@Log4j2
public class VentureApiManager {

    @Autowired
    private VentureInfoReository ventureInfoReository;

    @GetMapping("/api")
    public String callApi() {
        String result = "";
        try {
            String urlStr = "https://api.odcloud.kr/api/15084581/v1/uddi:fce46dc0-d056-4466-b2a3-c0222b0d6d3a?" +
                    "page=1" + "&perPage=10" + "&serviceKey=0gzS7HlAgLKTozgbo5ims%2BLzvqV1JFFi%2Bf4U5RHPao936LyED6swa1o6MnbTMaA18evjCl8dOF5GKJipHIrr1g%3D%3D";
            URL url = new URL(urlStr);

            BufferedReader bf;
            bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            result = bf.readLine();

            //JSON 파싱 객체를 생성
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(result);

            // jsonObject의 내용을 출력 (로그에 출력됨)
            log.info(jsonObject.toString());

            JSONArray infoArr = (JSONArray) jsonObject.get("data");

            // "item" 배열의 각 요소에 접근하여 데이터베이스에 저장합니다.
            for (Object item : infoArr) {
                // 각 요소를 JSONObject로 형변환합니다.
                if (item instanceof JSONObject) {
                    JSONObject itemObject = (JSONObject) item;

                    // JSONObject에서 필요한 정보를 추출합니다.
                    String code = (String) itemObject.get("신규_재확인코드");
                    String mainProduct = (String) itemObject.get("주생산품");
                    String area = (String) itemObject.get("지역");
                    String address = (String) itemObject.get("간략주소");
                    String registInstitution = (String) itemObject.get("벤처확인기관");
                    String endDate = (String) itemObject.get("벤처유효종료일");
                    String registType = (String) itemObject.get("벤처확인유형");
                    String typeName = (String) itemObject.get("업종명(10차)");
                    String typeName_spc = (String) itemObject.get("업종분류(기보)");
                    String name = (String) itemObject.get("업체명");
                    Long id = (long) itemObject.get("연번");
                    String owner = (String) itemObject.get("대표자명");
                    String startDate = (String) itemObject.get("벤처유효시작일");

                    // VentureInfo 객체를 생성합니다.
                    VentureInfo infoObj = new VentureInfo(code, mainProduct, area, address,
                            registInstitution, endDate, registType, typeName, typeName_spc,
                            name, id, owner, startDate);

                    // 데이터베이스에 저장합니다.
                    ventureInfoReository.save(infoObj);
                }else {
                    // JSONObject가 아닌 경우에 대한 처리를 수행합니다.
                    System.out.println("JSONObject 아님: " + item.toString());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/index";
    }
}


