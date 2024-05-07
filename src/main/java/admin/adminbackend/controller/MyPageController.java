package admin.adminbackend.controller;


import admin.adminbackend.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {
    private final MyPageService myPageService;




}
