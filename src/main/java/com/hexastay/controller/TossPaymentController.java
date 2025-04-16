package com.hexastay.controller;

import com.hexastay.dto.PaymentResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class TossPaymentController {

    @GetMapping("/payment")
    public String paymentPage() {
        return "toss/payment";
    }

    @GetMapping("/toss/success")
    public String paymentSuccess(@RequestParam String paymentKey,
                               @RequestParam String orderId,
                               @RequestParam Long amount,
                               Model model) {
        // PaymentResponse 객체 직접 생성
        PaymentResponse payment = new PaymentResponse(orderId, amount, LocalDateTime.now());
        model.addAttribute("payment", payment);
        return "toss/success";
    }

    @GetMapping("/toss/fail")
    public String paymentFail() {
        return "toss/fail";
    }
} 