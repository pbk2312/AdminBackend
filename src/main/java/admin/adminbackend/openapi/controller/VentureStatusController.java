package admin.adminbackend.openapi.controller;

import admin.adminbackend.openapi.service.UtilService.UtilService;
import admin.adminbackend.openapi.service.VentureStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
public class VentureStatusController {

    private final VentureStatusService ventureStatusService;

    @PostMapping("/api/ventureStatus")
    public JSONObject getCompanyNum(@RequestBody String b_no) {
        return ventureStatusService.getCompanyNum(b_no);
    }

}