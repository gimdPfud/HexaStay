package com.hexastay.service;

import com.hexastay.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(String paymentKey, String orderId, Long amount);
} 