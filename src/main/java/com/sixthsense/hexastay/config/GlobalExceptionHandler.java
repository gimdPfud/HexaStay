package com.sixthsense.hexastay.config;

import com.sixthsense.hexastay.util.exception.SoldOutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // REST 컨트롤러에서 발생하는 예외를 전역적으로 처리
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(SoldOutException.class) // 처리할 예외 클래스 지정
        public ResponseEntity<Map<String, String>> handleSoldOutException(SoldOutException ex) {
            log.warn("재고 부족 예외 발생: {}", ex.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", ex.getMessage());

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 또는 HttpStatus.CONFLICT
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, String>> handleException(Exception ex) {
            log.error("처리되지 않은 예외 발생", ex); // 서버 로그에는 에러 레벨 및 스택 트레이스 기록

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "요청 처리 중 오류가 발생했습니다. 관리자에게 문의하세요."); // 사용자 친화적 메시지

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

}
