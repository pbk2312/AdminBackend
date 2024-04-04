package admin.adminbackend.openapi.controller;

import admin.adminbackend.openapi.service.VentureApiManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

import static admin.adminbackend.util.RestResponse.success;

@RestController
@RequiredArgsConstructor
public class VentureApiController {

    private final VentureApiManager ventureApiManager;

    /*@GetMapping("open-api")
    public ResponseEntity<?> fetch() throws UnsupportedEncodingException {
        return success(ventureApiManager.fetch().getBody());
    }*/

}
