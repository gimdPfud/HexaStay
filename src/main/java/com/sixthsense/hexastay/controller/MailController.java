package com.sixthsense.hexastay.controller;

import com.sixthsense.hexastay.service.impl.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Log4j2
@Controller
@RequiredArgsConstructor
//메일 서비스 테스트용 로직
public class MailController {

    private final MailService mailService;

    @GetMapping("/review/mail")
    private String mailinputaaa() {



        return "mail"; // mail.html 필요 없음, 그냥 텍스트만 리턴돼도 됨
    }

    @PostMapping("/review/mail")
    public String sendTestMail(@RequestParam String email,
                               @RequestParam String name,
                               @RequestParam String room,
                               @RequestParam String checkIn,
                               @RequestParam String checkOut,
                               @RequestParam String password,
                               @RequestParam Long hotelRoomNum,
                               Model model) {

        // datetime-local 형식은 "2025-04-30T14:30" 같은 문자열이므로 LocalDateTime으로 파싱
        LocalDateTime checkInDateTime = LocalDateTime.parse(checkIn);
        LocalDateTime checkOutDateTime = LocalDateTime.parse(checkOut);

        // 메일 발송 서비스 호출
        mailService.sendRoomReservationEmailpa(
                email,
                name,
                room,
                checkInDateTime,
                checkOutDateTime,
                password,
                hotelRoomNum
        );

        model.addAttribute("result", "메일 발송 성공!");
        return "mail"; // mail.html로 결과 출력
    }
}
