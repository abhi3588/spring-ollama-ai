package com.example.demo.domain;

import lombok.Data;

@Data
public class Payment {
    private String paymentId;
    private String paymentDate;
    private String paymentAmount;
    private String currency;
    private String paymentStatus;
    private String companyId;
    private String userId;
    private String originatingAccount;
    private String recipientName;
    private String recipientAccount;
    private String recipientBankId;
}
