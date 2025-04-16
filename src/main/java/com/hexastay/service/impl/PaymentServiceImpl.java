package com.hexastay.service.impl;

import com.hexastay.dto.PaymentResponse;
import com.hexastay.service.PaymentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Override
    public PaymentResponse processPayment(String paymentKey, String orderId, Long amount) {
        // 실제 결제 처리 로직은 여기에 구현
        return new PaymentResponse(orderId, amount, LocalDateTime.now());
    }
} 