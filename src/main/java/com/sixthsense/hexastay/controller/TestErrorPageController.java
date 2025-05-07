package com.sixthsense.hexastay.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;

@Controller
public class TestErrorPageController {

    @GetMapping("/test/trigger-400")
    public void trigger400Error(HttpServletResponse response) throws IOException {

        response.sendError(HttpStatus.BAD_REQUEST.value(), "테스트용 400 Bad Request 오류입니다.");
    }

    @GetMapping("/test/trigger-401")
    public void trigger401Error(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "테스트용 401 Unauthorized 오류입니다.");
    }

    @GetMapping("/test/trigger-403")
    public void trigger403Error(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), "테스트용 403 Forbidden 오류입니다.");
    }

    // (참고) 500 Internal Server Error 테스트
    @GetMapping("/test/trigger-500")
    public String trigger500Error() {
        throw new RuntimeException("테스트용 500 Internal Server Error 입니다!");
    }
}