package admin.adminbackend.openapi.controller;

import admin.adminbackend.openapi.domain.VentureListInfo;
import admin.adminbackend.openapi.service.VentureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import admin.adminbackend.openapi.dto.VentureListInfoForm;

import java.io.IOException;
import java.net.MalformedURLException;

@Controller
@RequiredArgsConstructor
@Slf4j
public class VentureController {

    private final VentureService ventureService;

    @GetMapping("/ventures/new")
    public String newVenture(@ModelAttribute VentureListInfoForm form) {
        return "venture-form";
    }

    @PostMapping("/ventures/new")
    public String saveVenture(@ModelAttribute VentureListInfoForm form, RedirectAttributes redirectAttributes) throws IOException {
        Long ventureId = ventureService.saveVenture(form);
        redirectAttributes.addAttribute("ventureId", ventureId);
        return "redirect:/ventures/{ventureId}";
    }

    @GetMapping("/ventures/{id}")
    public String ventures(@PathVariable Long id, Model model) {
        //Venture venture = ventureService.getVentureById(id);
        VentureListInfo ventureListInfo = ventureService.getVentureById(id);
        model.addAttribute("ventureListInfo", ventureListInfo);
        return "venture-view";
    }

    @GetMapping("/attach/{id}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long id) throws MalformedURLException {
        return ventureService.downloadAttach(id);
    }
}
