package admin.adminbackend.openapi.controller;

import admin.adminbackend.openapi.dto.VentureListInfo;
import admin.adminbackend.openapi.service.VentureListInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    @RestController
    @RequestMapping("/api/venture")
    public class VentureListInfoController {

        @Autowired
        private VentureListInfoService ventureListInfoService;

        @GetMapping("/{name}")
        public VentureListInfo getCompanyByName(@PathVariable String name) {
            return ventureListInfoService.getCompanyByName(name);
        }

        @GetMapping("/all")
        public List<VentureListInfo> getAllCompanies() {
            return ventureListInfoService.getAllCompanies();
        }

        @PostMapping("/add")
        public VentureListInfo addCompany(@RequestBody VentureListInfo ventureListInfo) {
            return ventureListInfoService.saveCompany(ventureListInfo);
        }

        @DeleteMapping("/{id}")
        public void deleteCompany(@PathVariable Long id) {
            ventureListInfoService.deleteCompany(id);
        }
    }

