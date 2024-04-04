package admin.adminbackend.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@Log4j2
public class LogInController {


    @GetMapping("/login")
    public String login() {
        log.info("login....");
        return "ok";
    }


    @PostMapping("/login")
    public String loginPOST() {
        return "ok";
    }





}
