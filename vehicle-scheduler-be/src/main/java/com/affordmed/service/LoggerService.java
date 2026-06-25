package com.affordmed.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LoggerService {

    private static final String LOG_URL =
            "http://4.224.186.213/evaluation-service/logs";

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNYXBDbGFpbXMiOnsiYXVkIjoiaHR0cDovLzIwLjI0NC41Ni4xNDQvZXZhbHVhdGlvbi1zZXJ2aWNlIiwiZW1haWwiOiIyMzIxMWEwNTQ3QGJ2cml0LmFjLmluIiwiZXhwIjoxNzgyMzgxNzI3LCJpYXQiOjE3ODIzODA4MjcsImlzcyI6IkFmZm9yZCBNZWRpY2FsIFRlY2hub2xvZ2llcyBQcml2YXRlIExpbWl0ZWQiLCJqdGkiOiIyYjdkOTY2My0xMjZiLTQzNzAtYTA1Zi0wNDcwNzNkMzNjY2QiLCJsb2NhbGUiOiJlbi1JTiIsIm5hbWUiOiJidXJ1Z3VsYSByYWdoYXZlbmRyYSIsInN1YiI6ImQ0MzU0MzBlLTQ2ZTctNDVlYS04MWJlLWNhODc3OTFkYjJmYSJ9LCJlbWFpbCI6IjIzMjExYTA1NDdAYnZyaXQuYWMuaW4iLCJuYW1lIjoiYnVydWd1bGEgcmFnaGF2ZW5kcmEiLCJyb2xsTm8iOiIyMzIxMWEwNTQ3IiwiYWNjZXNzQ29kZSI6ImFoWGp2cCIsImNsaWVudElEIjoiZDQzNTQzMGUtNDZlNy00NWVhLTgxYmUtY2E4Nzc5MWRiMmZhIiwiY2xpZW50U2VjcmV0IjoielhVS0hKV2R2bVdiUUtrbSJ9.W3MgXUSSFp0J50HIyuDEUHTvuEdBXW7o3nGrrRRXfVo";
    // private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNYXBDbGFpbXMiOnsiYXVkIjoiaHR0cDovLzIwLjI0NC41Ni4xNDQvZXZhbHVhdGlvbi1zZXJ2aWNlIiwiZW1haWwiOiIyMzIxMWEwNTQ3QGJ2cml0LmFjLmluIiwiZXhwIjoxNzgyMzc0MzM4LCJpYXQiOjE3ODIzNzM0MzgsImlzcyI6IkFmZm9yZCBNZWRpY2FsIFRlY2hub2xvZ2llcyBQcml2YXRlIExpbWl0ZWQiLCJqdGkiOiI1MTI2OWJiZC04MjhlLTRiYzQtOTA0NS0zOWQzMzA2ZTZkZGQiLCJsb2NhbGUiOiJlbi1JTiIsIm5hbWUiOiJidXJ1Z3VsYSByYWdoYXZlbmRyYSIsInN1YiI6ImQ0MzU0MzBlLTQ2ZTctNDVlYS04MWJlLWNhODc3OTFkYjJmYSJ9LCJlbWFpbCI6IjIzMjExYTA1NDdAYnZyaXQuYWMuaW4iLCJuYW1lIjoiYnVydWd1bGEgcmFnaGF2ZW5kcmEiLCJyb2xsTm8iOiIyMzIxMWEwNTQ3IiwiYWNjZXNzQ29kZSI6ImFoWGp2cCIsImNsaWVudElEIjoiZDQzNTQzMGUtNDZlNy00NWVhLTgxYmUtY2E4Nzc5MWRiMmZhIiwiY2xpZW50U2VjcmV0IjoielhVS0hKV2R2bVdiUUtrbSJ9.efrVjPIed4KZaqD9ngFP-oRJCMK-Go009yYK1HJVgU0";
 //  private static final String TOKEN ="ahXjvp";
    @Autowired
    private RestTemplate restTemplate;

    public void log(String stack, String level,
                    String packageName, String message) {
        try {
            String body = String.format(
                    "{\"stack\":\"%s\",\"level\":\"%s\",\"package\":\"%s\",\"message\":\"%s\"}",
                    stack, level, packageName, message
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", TOKEN);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            restTemplate.exchange(
                    LOG_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

        } catch (Exception e) {
            System.out.println("Logging Failed: " + e.getMessage());
        }
    }
}