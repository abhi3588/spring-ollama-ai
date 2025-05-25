package com.example.demo.service;

import com.example.demo.client.OllamaClient;
import com.example.demo.domain.Payment;
import com.example.demo.domain.Payments;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final OllamaClient ollamaClient;
    private final ObjectMapper objectMapper;

    public String getFilteredPayments(String prompt) throws Exception {
        return ollamaClient.getFilteredPayments(prompt);
    }

    public List<Payment> getPayments() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("payment_data.json");
        var payments =  objectMapper.readValue(is, Payments.class);
        return payments.getPayments();
    }
}
