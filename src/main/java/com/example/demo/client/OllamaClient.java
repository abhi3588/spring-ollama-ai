package com.example.demo.client;

import com.example.demo.domain.Payment;
import com.example.demo.domain.Payments;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class OllamaClient {

    private final OllamaChatModel chatModel;

    @Autowired
    public OllamaClient(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Payment> getFilteredPayments(String prompt) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("payment_data.json");
        Payments paymentJson = objectMapper.readValue(is, Payments.class);
        String paymentsJsonString = objectMapper.writeValueAsString(paymentJson);
        System.out.println("paymentsJsonString :: " + paymentsJsonString);

        if (!StringUtils.hasLength(prompt)) {
            return Collections.EMPTY_LIST;
        }

        String fullPrompt = "Act as a strict JSON filter.\n" +
                "\n" +
                "You will receive:\n" +
                "1. A JSON String with a field called \"payments\", which contains a list of objects.\n" +
                "2. A filtering condition in plain text.\n" +
                "\n" +
                "Instructions:\n" +
                "- Parse the input JSON.\n" +
                "- Process JSON data sequentially.\n" +
                "- Do NOT interchange any value between JSON objects\n" +
                "- Return ONLY those objects inside \"payments\" where it EXACTLY matches the given condition.\n" +
                "- Return the output in this format: { \"payments\": [ ... ] }.\n" +
                "- Do NOT include any explanation, comments, or formatting.\n" +
                "- Ensure output is STRICT JSON.\n" +
                "\n" +
                "Input JSON (as string):\n" +
                paymentsJsonString +
                "\n" +
                "Filtering condition:\n" +
                prompt;

        System.out.println("Prompt :: " + fullPrompt);
        Prompt p = new Prompt(fullPrompt, OllamaOptions.builder().build());
        ChatResponse response = this.chatModel.call(p);
        String textContent = response.getResult().getOutput().getText();
        System.out.println("Text Content :: " + textContent);

        Payments filteredPayments = objectMapper.readValue(textContent, Payments.class);
        return filteredPayments.getPayments();
    }
}