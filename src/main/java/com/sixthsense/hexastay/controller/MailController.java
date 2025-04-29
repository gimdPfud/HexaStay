package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.service.impl.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Log4j2
@Controller
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @GetMapping("/review/mail")
    private String mailinputaaa() {



        return "mail"; // mail.html 필요 없음, 그냥 텍스트만 리턴돼도 됨
    }

    @PostMapping("/review/mail")
    private String mailinput() {

        try {
            mailService.sendRoomPasswordEmail("kimbbuhhwan@gmail.com", "1234");
            log.info("✅ 메일 발송 컨트롤러 정상 호출 완료");
        } catch (Exception e) {
            log.error("❌ 메일 발송 컨트롤러 오류", e);
        }

        return "mail"; // mail.html 필요 없음, 그냥 텍스트만 리턴돼도 됨
    }
}
