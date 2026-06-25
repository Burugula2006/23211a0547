package com.affordmed.service;

import com.affordmed.dto.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class NotificationService {

    private static final String NOTIFICATION_API =
            "http://4.224.186.213/evaluation-service/notifications";

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNYXBDbGFpbXMiOnsiYXVkIjoiaHR0cDovLzIwLjI0NC41Ni4xNDQvZXZhbHVhdGlvbi1zZXJ2aWNlIiwiZW1haWwiOiIyMzIxMWEwNTQ3QGJ2cml0LmFjLmluIiwiZXhwIjoxNzgyMzgxNzI3LCJpYXQiOjE3ODIzODA4MjcsImlzcyI6IkFmZm9yZCBNZWRpY2FsIFRlY2hub2xvZ2llcyBQcml2YXRlIExpbWl0ZWQiLCJqdGkiOiIyYjdkOTY2My0xMjZiLTQzNzAtYTA1Zi0wNDcwNzNkMzNjY2QiLCJsb2NhbGUiOiJlbi1JTiIsIm5hbWUiOiJidXJ1Z3VsYSByYWdoYXZlbmRyYSIsInN1YiI6ImQ0MzU0MzBlLTQ2ZTctNDVlYS04MWJlLWNhODc3OTFkYjJmYSJ9LCJlbWFpbCI6IjIzMjExYTA1NDdAYnZyaXQuYWMuaW4iLCJuYW1lIjoiYnVydWd1bGEgcmFnaGF2ZW5kcmEiLCJyb2xsTm8iOiIyMzIxMWEwNTQ3IiwiYWNjZXNzQ29kZSI6ImFoWGp2cCIsImNsaWVudElEIjoiZDQzNTQzMGUtNDZlNy00NWVhLTgxYmUtY2E4Nzc5MWRiMmZhIiwiY2xpZW50U2VjcmV0IjoielhVS0hKV2R2bVdiUUtrbSJ9.W3MgXUSSFp0J50HIyuDEUHTvuEdBXW7o3nGrrRRXfVo";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoggerService loggerService;

    // ── Fetch all notifications from API ─────────────────────────
    public List<Notification> fetchNotifications() {

        loggerService.log("backend", "info", "service",
                "Fetching notifications from API");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", TOKEN);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, List<Notification>>> response =
                restTemplate.exchange(
                        NOTIFICATION_API,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference <Map<String, List<Notification>>>() {}
                );

        Map<String, List<Notification>> body = response.getBody();
        return body != null ? body.get("notifications")
                : Collections.emptyList();
    }

    private int getWeight(String type) {
        if (type == null) return 0;
        switch (type) {
            case "Placement": return 3;
            case "Result":    return 2;
            case "Event":     return 1;
            default:          return 0;
        }
    }


    public List<Notification> getTopN(int n) {

        loggerService.log("backend", "info", "service",
                "Computing top " + n + " priority notifications");

        List<Notification> all = fetchNotifications();

        // Max-heap: higher weight = higher priority
        // For same weight, more recent timestamp wins
        PriorityQueue<Notification> maxHeap = new PriorityQueue<>(
                (a, b) -> {
                    int weightDiff = getWeight(b.getType())
                            - getWeight(a.getType());
                    if (weightDiff != 0) return weightDiff;
                    // same weight → compare timestamps (newer first)
                    return b.getTimestamp().compareTo(a.getTimestamp());
                }
        );

        for (Notification notification : all) {
            maxHeap.offer(notification);
        }

        List<Notification> result = new ArrayList<>();
        int count = 0;
        while (!maxHeap.isEmpty() && count < n) {
            result.add(maxHeap.poll());
            count++;
        }

        loggerService.log("backend", "info", "service",
                "Returning top " + result.size() + " notifications");

        return result;
    }
}