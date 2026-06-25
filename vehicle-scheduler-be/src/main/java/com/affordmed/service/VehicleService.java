package com.affordmed.service;

import com.affordmed.dto.Depot;
import com.affordmed.dto.ScheduleResponse;
import com.affordmed.dto.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class VehicleService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoggerService loggerService;

    private static final String DEPOT_API =
            "http://4.224.186.213/evaluation-service/depots?accessCode=ahXjvp";

    private static final String VEHICLE_API =
            "http://4.224.186.213/evaluation-service/vehicles?accessCode=ahXjvp";



    // private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNYXBDbGFpbXMiOnsiYXVkIjoiaHR0cDovLzIwLjI0NC41Ni4xNDQvZXZhbHVhdGlvbi1zZXJ2aWNlIiwiZW1haWwiOiIyMzIxMWEwNTQ3QGJ2cml0LmFjLmluIiwiZXhwIjoxNzgyMzc0MzM4LCJpYXQiOjE3ODIzNzM0MzgsImlzcyI6IkFmZm9yZCBNZWRpY2FsIFRlY2hub2xvZ2llcyBQcml2YXRlIExpbWl0ZWQiLCJqdGkiOiI1MTI2OWJiZC04MjhlLTRiYzQtOTA0NS0zOWQzMzA2ZTZkZGQiLCJsb2NhbGUiOiJlbi1JTiIsIm5hbWUiOiJidXJ1Z3VsYSByYWdoYXZlbmRyYSIsInN1YiI6ImQ0MzU0MzBlLTQ2ZTctNDVlYS04MWJlLWNhODc3OTFkYjJmYSJ9LCJlbWFpbCI6IjIzMjExYTA1NDdAYnZyaXQuYWMuaW4iLCJuYW1lIjoiYnVydWd1bGEgcmFnaGF2ZW5kcmEiLCJyb2xsTm8iOiIyMzIxMWEwNTQ3IiwiYWNjZXNzQ29kZSI6ImFoWGp2cCIsImNsaWVudElEIjoiZDQzNTQzMGUtNDZlNy00NWVhLTgxYmUtY2E4Nzc5MWRiMmZhIiwiY2xpZW50U2VjcmV0IjoielhVS0hKV2R2bVdiUUtrbSJ9.efrVjPIed4KZaqD9ngFP-oRJCMK-Go009yYK1HJVgU0";
  //  private static final String TOKEN = "ahXjvp";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNYXBDbGFpbXMiOnsiYXVkIjoiaHR0cDovLzIwLjI0NC41Ni4xNDQvZXZhbHVhdGlvbi1zZXJ2aWNlIiwiZW1haWwiOiIyMzIxMWEwNTQ3QGJ2cml0LmFjLmluIiwiZXhwIjoxNzgyMzgxNzI3LCJpYXQiOjE3ODIzODA4MjcsImlzcyI6IkFmZm9yZCBNZWRpY2FsIFRlY2hub2xvZ2llcyBQcml2YXRlIExpbWl0ZWQiLCJqdGkiOiIyYjdkOTY2My0xMjZiLTQzNzAtYTA1Zi0wNDcwNzNkMzNjY2QiLCJsb2NhbGUiOiJlbi1JTiIsIm5hbWUiOiJidXJ1Z3VsYSByYWdoYXZlbmRyYSIsInN1YiI6ImQ0MzU0MzBlLTQ2ZTctNDVlYS04MWJlLWNhODc3OTFkYjJmYSJ9LCJlbWFpbCI6IjIzMjExYTA1NDdAYnZyaXQuYWMuaW4iLCJuYW1lIjoiYnVydWd1bGEgcmFnaGF2ZW5kcmEiLCJyb2xsTm8iOiIyMzIxMWEwNTQ3IiwiYWNjZXNzQ29kZSI6ImFoWGp2cCIsImNsaWVudElEIjoiZDQzNTQzMGUtNDZlNy00NWVhLTgxYmUtY2E4Nzc5MWRiMmZhIiwiY2xpZW50U2VjcmV0IjoielhVS0hKV2R2bVdiUUtrbSJ9.W3MgXUSSFp0J50HIyuDEUHTvuEdBXW7o3nGrrRRXfVo";

    public List<Depot> getDepots() {
        loggerService.log("backend", "info", "service",
                "Fetching depots from API");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", TOKEN);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // API returns { "depots": [...] } so we need a wrapper
        ResponseEntity<Map<String, List<Depot>>> response =
                restTemplate.exchange(
                        DEPOT_API,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<Map<String, List<Depot>>>() {}
                );

        Map<String, List<Depot>> body = response.getBody();
        return body != null ? body.get("depots") : Collections.emptyList();
    }


    public List<Vehicle> getVehicles() {
        loggerService.log("backend", "info", "service",
                "Fetching vehicles from API");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", TOKEN);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, List<Vehicle>>> response =
                restTemplate.exchange(
                        VEHICLE_API,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<Map<String, List<Vehicle>>>() {}
                );

        Map<String, List<Vehicle>> body = response.getBody();
        return body != null ? body.get("vehicles") : Collections.emptyList();
    }


    public List<ScheduleResponse> schedule() {

        loggerService.log("backend", "info", "service",
                "Starting schedule computation");

        List<Depot> depots = getDepots();
        List<Vehicle> vehicles = getVehicles();

        loggerService.log("backend", "info", "service",
                "Depots: " + depots.size() +
                        ", Vehicles: " + vehicles.size());

        List<ScheduleResponse> results = new ArrayList<>();

        for (Depot depot : depots) {

            int maxHours = depot.getMechanicHours();

            loggerService.log("backend", "info", "service",
                    "Scheduling depot " + depot.getId() +
                            " with budget " + maxHours + "h");


            int[][] dp = buildKnapsackTable(vehicles, maxHours);


            List<String> selected = traceback(dp, vehicles, maxHours);


            int hoursUsed = 0;
            int totalImpact = 0;
            for (Vehicle v : vehicles) {
                if (selected.contains(v.getTaskId())) {
                    hoursUsed += v.getDuration();
                    totalImpact += v.getImpact();
                }
            }

            loggerService.log("backend", "info", "service",
                    "Depot " + depot.getId() +
                            " → selected " + selected.size() +
                            " vehicles, impact=" + totalImpact +
                            ", hours=" + hoursUsed);

            results.add(new ScheduleResponse(
                    depot.getId(),
                    maxHours,
                    hoursUsed,
                    totalImpact,
                    selected
            ));
        }

        return results;
    }

    private int[][] buildKnapsackTable(List<Vehicle> vehicles,
                                       int maxHours) {
        int n = vehicles.size();
        int[][] dp = new int[n + 1][maxHours + 1];

        for (int i = 1; i <= n; i++) {
            Vehicle vehicle = vehicles.get(i - 1);
            int duration = vehicle.getDuration();
            int impact = vehicle.getImpact();

            for (int h = 0; h <= maxHours; h++) {
                if (duration <= h) {
                    dp[i][h] = Math.max(
                            dp[i - 1][h],
                            impact + dp[i - 1][h - duration]
                    );
                } else {
                    dp[i][h] = dp[i - 1][h];
                }
            }
        }
        return dp;
    }

    private List<String> traceback(int[][] dp,
                                   List<Vehicle> vehicles,
                                   int maxHours) {
        List<String> selected = new ArrayList<>();
        int h = maxHours;
        int n = vehicles.size();

        for (int i = n; i >= 1; i--) {

            if (dp[i][h] != dp[i - 1][h]) {
                Vehicle v = vehicles.get(i - 1);
                selected.add(v.getTaskId());
                h -= v.getDuration();
            }
        }

        return selected;
    }
}