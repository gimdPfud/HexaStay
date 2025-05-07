package com.sixthsense.hexastay.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    @Autowired
    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // 오류 속성 가져오기
        ServletWebRequest webRequest = new ServletWebRequest(request);
        ErrorAttributeOptions options = ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.STACK_TRACE,
                ErrorAttributeOptions.Include.BINDING_ERRORS
        );
        Map<String, Object> errorAttributesMap = errorAttributes.getErrorAttributes(webRequest, options);

        // 모델에 모두 추가
        model.addAllAttributes(errorAttributesMap);

        // 상태 코드 확인
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        String errorPage = "error/error"; // 기본 에러 페이지

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorPage = "error/404";
                model.addAttribute("customMessage", "요청하신 페이지를 찾을 수 없습니다. URL을 다시 확인해주세요.");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorPage = "error/500";
                model.addAttribute("customMessage", "서버 내부에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                errorPage = "error/403";
                model.addAttribute("customMessage", "이 페이지에 접근할 권한이 없습니다.");
            }
        }

        return errorPage;
    }
}
