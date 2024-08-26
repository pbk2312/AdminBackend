package admin.adminbackend.openapi.controller;

import admin.adminbackend.openapi.Repository.VentureListInfoRepository;
import admin.adminbackend.openapi.domain.VentureListInfo;
import admin.adminbackend.openapi.service.VentureListService;
import lombok.extern.log4j.Log4j2;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//import net.minidev.json.JSONArray;
//import net.minidev.json.JSONObject;
//import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@RestController
@Log4j2
@RequestMapping("/venture")
public class VentureListController {

    @Autowired
    private VentureListService ventureListService;

   /* private final VentureListService ventureListService;

    // 생성자 주입 (Constructor Injection)
    public VentureListController(VentureListService ventureListService) {
        this.ventureListService = ventureListService;
    }*/

    @GetMapping("/list")
    @ResponseBody
    public JSONArray callApi(int page) {
        return ventureListService.callApi(page);
    }

    /*@GetMapping("/{name}")
    public VentureListInfo getCompanyByName(@PathVariable String name) {
        log.info("기업 정보 조회...");
        return ventureListService.getCompanyByName(name);
    }*/

    @GetMapping("/{id}")
    public VentureListInfo getCompanyById(@PathVariable Long id) {
        log.info("기업 정보 조회...");
        return ventureListService.getCompanyById(id);
    }

    @GetMapping("/all")
    @ResponseBody
    public Page<VentureListInfo> getAllVentureList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return ventureListService.getAllVentureList(page, size);
    }
}