package admin.adminbackend.service.venture;

import admin.adminbackend.domain.Member;
import admin.adminbackend.controller.venture.VentureStatusController;
import admin.adminbackend.util.UtilService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class VentureStatusService {
    public static JSONObject getCompanyNum(@org.springframework.web.bind.annotation.RequestBody String b_no) {
        //okhttp3으로 통신
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");

        //객체 안에 배열 { "b_no" : ["3423423"] }
        //요청 형식이 json 배열임
        List<String> arry = new ArrayList<>();
        arry.add(b_no); //{} 객체, [{},{},{}] 배열(객체 묶음)
        JSONObject requestBody = new JSONObject();
        requestBody.put("b_no",arry); //b_no 배열 이름, 객체 1개
        log.info("b_no={}", arry);

        RequestBody body = RequestBody.create(requestBody.toString(),mediaType);
        Request request = new Request.Builder()
                .url("http://api.odcloud.kr/api/nts-businessman/v1/status?serviceKey=0gzS7HlAgLKTozgbo5ims%2BLzvqV1JFFi%2Bf4U5RHPao936LyED6swa1o6MnbTMaA18evjCl8dOF5GKJipHIrr1g%3D%3D")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        //결과 탐색
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            log.info("Response Body: {}", responseBody);

            JSONObject jsonResponse = UtilService.stringToJson(responseBody);
            JSONArray dataArray = (JSONArray) jsonResponse.get("data");
            if (dataArray != null && !dataArray.isEmpty()) {
                JSONObject dataObject = (JSONObject) dataArray.get(0);
                String b_stt = (String) dataObject.get("b_stt");
                log.info("b_stt: {}", b_stt);

                // b_stt만 포함된 JSON 응답 생성
                JSONObject result = new JSONObject();
                result.put("b_stt", b_stt);

                // 여기다가 기업 승급


                return result;

            } else {
                log.error("Data array is null or empty");
                //return null;
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("error", "No data found");
                return errorResponse;
            }
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
            UtilService.ExceptionValue(e.getMessage(), VentureStatusController.class);
            //return null;
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "An error occurred while processing the request.");
            return errorResponse;
        }
    }
}

