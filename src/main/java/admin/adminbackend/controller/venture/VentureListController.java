package admin.adminbackend.controller.venture;

import admin.adminbackend.domain.kim.VentureListInfo;
import admin.adminbackend.service.venture.VentureListService;
import lombok.extern.log4j.Log4j2;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequestMapping("/venture")
public class VentureListController {

    @Autowired
    private VentureListService ventureListService;

    @GetMapping("/list")
    @ResponseBody
    public JSONArray callApi(Integer page) {
        return ventureListService.callApi(page);
    }

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