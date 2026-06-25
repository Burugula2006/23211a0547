package com.affordmed.service;

import com.affordmed.dto.LogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LoggerService {

    private static final String LOG_URL =
            "http://4.224.186.213/evaluation-service/logs";

    private static final String TOKEN = "ahXjvp";
    @Autowired
    private RestTemplate restTemplate;

    public void log(String stack, String level,
                    String packageName, String message) {
        try {
            LogRequest request = new LogRequest(
                    stack, level, packageName, message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", TOKEN);

            HttpEntity<LogRequest> entity =
                    new HttpEntity<>(request, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            LOG_URL,
                            HttpMethod.POST,
                            entity,
                            String.class
                    );

            System.out.println("Log Response: " + response.getBody());

        } catch (Exception e) {
            System.out.println("Logging Failed: " + e.getMessage());
        }
    }
}