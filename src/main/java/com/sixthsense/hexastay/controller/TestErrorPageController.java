package com.sixthsense.hexastay.controller;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.IOException;

/**************************************************
 * 클래스명 : TestErrorPageController
 * 기능   : HTTP 오류 페이지 테스트를 위한 컨트롤러입니다.
 * 의도적으로 다양한 HTTP 상태 코드(400, 401, 403, 500, 502, 503)를 발생시켜
 * 설정된 오류 페이지가 정상적으로 동작하는지 확인하는 데 사용됩니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-05-07
 * 수정일 : 2025-05-09
 * 입출력 변수 설계 : 김윤겸
 **************************************************/

@Controller
public class TestErrorPageController {

    /**************************************************
     * 메소드명 : trigger400Error
     * 400 Bad Request 오류 발생 테스트
     * 기능: HTTP 400 (Bad Request) 오류를 의도적으로 발생시켜 해당 오류 페이지가
     * 정상적으로 표시되는지 테스트합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-05-07
     * 수정일 : -
     **************************************************/

    @GetMapping("/test/trigger-400")
    public void trigger400Error(HttpServletResponse response) throws IOException {

        response.sendError(HttpStatus.BAD_REQUEST.value(), "테스트용 400 Bad Request 오류입니다.");
    }

    /**************************************************
     * 메소드명 : trigger401Error
     * 401 Unauthorized 오류 발생 테스트
     * 기능: HTTP 401 (Unauthorized) 오류를 의도적으로 발생시켜 해당 오류 페이지가
     * 정상적으로 표시되는지 테스트합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-05-07
     * 수정일 : -
     **************************************************/

    @GetMapping("/test/trigger-401")
    public void trigger401Error(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "테스트용 401 Unauthorized 오류입니다.");
    }

    /**************************************************
     * 메소드명 : trigger403Error
     * 403 Forbidden 오류 발생 테스트
     * 기능: HTTP 403 (Forbidden) 오류를 의도적으로 발생시켜 해당 오류 페이지가
     * 정상적으로 표시되는지 테스트합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-05-07
     * 수정일 : -
     **************************************************/

    @GetMapping("/test/trigger-403")
    public void trigger403Error(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), "테스트용 403 Forbidden 오류입니다.");
    }

    /**************************************************
     * 메소드명 : trigger500Error
     * 500 Internal Server Error 발생 테스트
     * 기능: RuntimeException을 발생시켜 HTTP 500 (Internal Server Error) 오류를
     * 의도적으로 발생시키고, 해당 오류 페이지가 정상적으로 표시되는지 테스트합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-05-07
     * 수정일 : -
     **************************************************/

    // (참고) 500 Internal Server Error 테스트
    @GetMapping("/test/trigger-500")
    public String trigger500Error() {
        throw new RuntimeException("테스트용 500 Internal Server Error 입니다!");
    }

    /**************************************************
     * 메소드명 : trigger502Error
     * 502 Bad Gateway 오류 발생 테스트
     * 기능: HTTP 502 (Bad Gateway) 오류를 의도적으로 발생시켜 해당 오류 페이지가
     * 정상적으로 표시되는지 테스트합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-05-07
     * 수정일 : -
     **************************************************/

    @GetMapping("/test/trigger-502")
    public void trigger502Error(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_GATEWAY.value(), "테스트용 502 Bad Gateway 오류입니다.");
    }

    /**************************************************
     * 메소드명 : trigger503Error
     * 503 Service Unavailable 오류 발생 테스트
     * 기능: HTTP 503 (Service Unavailable) 오류를 의도적으로 발생시켜 해당 오류 페이지가
     * 정상적으로 표시되는지 테스트합니다.
     * 작성자 : 김윤겸
     * 등록일 : 2025-05-07
     * 수정일 : -
     **************************************************/

    @GetMapping("/test/trigger-503")
    public void trigger503Error(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.SERVICE_UNAVAILABLE.value(), "테스트용 503 Service Unavailable 오류입니다.");
    }
}