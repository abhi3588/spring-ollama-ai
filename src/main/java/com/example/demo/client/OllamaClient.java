package com.example.demo.client;

import com.example.demo.domain.Payments;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OllamaClient {

    private final OllamaChatModel chatModel;

    @Autowired
    public OllamaClient(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getFilteredPayments(String prompt) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("payment_data.json");
        Payments paymentJson = objectMapper.readValue(is, Payments.class);
        String paymentsJsonString = objectMapper.writeValueAsString(paymentJson);
        System.out.println("paymentsJsonString :: " + paymentsJsonString);

        if (!StringUtils.hasLength(prompt)) {
            return "";
        }

        String fullPrompt = """
                    You are given the following JSON object containing a `payments` array:
                    
                    %s

                    Your task is to:
                    1. Parse the `payments` array.
                    2. Count the occurrences of each unique value in the `paymentStatus` field.
                    3. Generate a JSX-compatible React string using `react-chartjs-2` to render a Pie chart.
                    4. The output should only include a `<div>` element structured exactly like this:

                       <div className="chart-container">
                         <h2 style={{ textAlign: "center" }}>Payment Status Chart</h2>
                         <Pie
                           data={{
                             labels: Object.keys(paymentStatusCount),
                             datasets: [
                               {
                                 data: Object.values(paymentStatusCount),
                                 backgroundColor: [
                                   "#36A2EB",
                                   "#FF6384",
                                   "#FFCE56",
                                   "#4BC0C0",
                                   "#9966FF",
                                   "#FF9F40",
                                 ],
                                 borderWidth: 1,
                               },
                             ],
                           }}
                         />
                       </div>

                    Notes:
                    - Replace `paymentStatusCount` with the actual counts as a JS object.
                    - Do NOT return any explanation, text, or code outside the `<div>` element.
                    - The output must be valid React JSX.

                    Output only the full JSX `<div>` element as described above.
                """.formatted(paymentsJsonString);

        System.out.println("Prompt :: " + fullPrompt);
        Prompt p = new Prompt(fullPrompt, OllamaOptions.builder().build());
        ChatResponse response = this.chatModel.call(p);
        String textContent = response.getResult().getOutput().getText();
        System.out.println("Text Content :: " + textContent);

        String jsx = extractJsx(textContent);
        System.out.println("Extracted JSX:\n" + jsx);

        return jsx;
    }

    public static String extractJsx(String response) {
        if (response == null || response.isBlank()) {
            return "";
        }

        // Step 1: Strip triple backticks if present
        response = response.replaceAll("(?i)```jsx", "")
                .replaceAll("```", "");

        // Step 2: Regex to find <div className="chart-container"> ... </div>
        Pattern pattern = Pattern.compile("<div className=\\\"chart-container\\\">[\\s\\S]*?</div>");
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            return matcher.group(0).trim();
        }

        return ""; // fallback if no JSX div block found
    }

}