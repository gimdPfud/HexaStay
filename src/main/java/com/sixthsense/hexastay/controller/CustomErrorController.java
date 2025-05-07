package com.sixthsense.hexastay.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);
    private final ErrorAttributes errorAttributes;

    @Autowired
    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        ErrorAttributeOptions options = ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.STACK_TRACE, // 운영 시 주의!
                ErrorAttributeOptions.Include.BINDING_ERRORS
        );
        Map<String, Object> errorAttributesMap = this.errorAttributes.getErrorAttributes(new ServletWebRequest(request), options);
        model.addAllAttributes(errorAttributesMap);
        logger.debug("Error Attributes passed to model: {}", errorAttributesMap);

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorPage = "error/error"; // 기본 에러 페이지

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            logger.info("Handling error with status code: {}", statusCode);

            if (statusCode == HttpStatus.NOT_FOUND.value()) { // 404
                errorPage = "error/404";
                model.addAttribute("customMessage", "요청하신 페이지를 찾을 수 없습니다. URL을 다시 확인해주세요.");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) { // 500
                errorPage = "error/500";
                model.addAttribute("customMessage", "서버 내부에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) { // 403
                errorPage = "error/403";
                model.addAttribute("customMessage", "이 페이지에 접근할 수 있는 권한이 없습니다.");
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) { // 401
                errorPage = "error/401";
                model.addAttribute("customMessage", "로그인이 필요한 서비스입니다. 먼저 로그인을 진행해주세요.");
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) { // 400
                errorPage = "error/400";
                model.addAttribute("customMessage", "요청 내용이 잘못되었습니다. 입력값을 다시 확인해주세요.");
            } else if (statusCode == HttpStatus.BAD_GATEWAY.value()) {
                errorPage = "error/502";
                model.addAttribute("customMessage", "서버 게이트웨이 또는 프록시 서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
            } else if (statusCode == HttpStatus.SERVICE_UNAVAILABLE.value()) {
                errorPage = "error/503";
                model.addAttribute("customMessage", "현재 서비스를 이용할 수 없습니다. 서버 점검 중이거나 과부하 상태일 수 있습니다.");
            }
            // 다른 특정 상태 코드에 대한 처리를 원하면 여기에 추가할 수 있습니다.

        } else {
            logger.warn("에러코드를 찾을 수 없음.");
            model.addAttribute("customMessage", "알 수 없는 오류가 발생했습니다.");
        }

        return errorPage;
    }
}
