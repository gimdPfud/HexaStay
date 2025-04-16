package com.hexastay.dto;

import java.time.LocalDateTime;

public class PaymentResponse {
    private String orderId;
    private Long amount;
    private LocalDateTime paymentDate;

    public PaymentResponse(String orderId, Long amount, LocalDateTime paymentDate) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getAmount() {
        return amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
} 