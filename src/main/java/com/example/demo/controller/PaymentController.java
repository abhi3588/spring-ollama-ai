package com.example.demo.controller;

import com.example.demo.domain.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/filter")
    public String filterPayments(@RequestParam String prompt) throws Exception {
        return paymentService.getFilteredPayments(prompt);
    }

    @GetMapping("/list")
    public List<Payment> getPayments() throws Exception {
        return paymentService.getPayments();
    }
}
