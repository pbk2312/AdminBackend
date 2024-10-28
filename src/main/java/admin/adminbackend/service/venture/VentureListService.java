package admin.adminbackend.service.venture;

import admin.adminbackend.repository.ventrue.VentureListInfoRepository;
import admin.adminbackend.domain.kim.VentureListInfo;
import admin.adminbackend.exception.CompanyNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@Service
@Log4j2
@RequiredArgsConstructor
public class VentureListService {


    private final VentureListInfoRepository ventureListInfoRepository;
    @Transactional
    public JSONArray callApi(int page) {
        JSONArray result = new JSONArray();
        try {
            String urlStr = "https://api.odcloud.kr/api/15084581/v1/uddi:fce46dc0-d056-4466-b2a3-c0222b0d6d3a?" +
                    "page=" + page + "&perPage=5000" + "&serviceKey=0gzS7HlAgLKTozgbo5ims%2BLzvqV1JFFi%2Bf4U5RHPao936LyED6swa1o6MnbTMaA18evjCl8dOF5GKJipHIrr1g%3D%3D";
            URL url = new URL(urlStr);

            BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = bf.readLine()) != null) {
                response.append(inputLine);
            }
            bf.close();

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());
            log.info(jsonObject.toString());

            JSONArray infoArr = (JSONArray) jsonObject.get("data");

            for (Object item : infoArr) {
                if (item instanceof JSONObject) {
                    JSONObject itemObject = (JSONObject) item;

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

                    VentureListInfo infoObj = new VentureListInfo(code, mainProduct, area, address,
                            registInstitution, endDate, registType, typeName, typeName_spc,
                            name, id, owner, startDate, null, null, null,
                            null, null, null);

                    if (!ventureListInfoRepository.existsById(id)) {
                        ventureListInfoRepository.save(infoObj);
                    }

                    result.add(itemObject);
                } else {
                    System.out.println("JSONObject 아님: " + item.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*public VentureListInfo getCompanyByName(String name) {
        return ventureListInfoRepository.findByName(name);
    }*/

    public Page<VentureListInfo> getAllVentureList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ventureListInfoRepository.findAll(pageable);
    }

    public VentureListInfo getCompanyById(Long id) {
        return ventureListInfoRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("해당 ID로 회사를 찾을 수 없습니다."));
    }



}
