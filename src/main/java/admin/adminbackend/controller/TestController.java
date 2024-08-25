package admin.adminbackend.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {


    @GetMapping("/testForm")
    public String test() {
        return "test";
    }

    @GetMapping("/IRCheckTest")
    public String IRCheckTest() {
        return "IRCheckTest";
    }


    @GetMapping("/paymentTest")
    public String paymentTest(){
        return "paymentTest";
    }
}
