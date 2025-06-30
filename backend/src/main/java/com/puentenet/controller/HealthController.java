package com.puentenet.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.puentenet.repository.InstrumentRepository;
import com.puentenet.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Health check", description = "Check if the application is running")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        
        try {
            // Check database connectivity
            long instrumentCount = instrumentRepository.count();
            long userCount = userRepository.count();
            
            Map<String, Object> database = new HashMap<>();
            database.put("status", "UP");
            database.put("instruments", instrumentCount);
            database.put("users", userCount);
            
            health.put("database", database);
        } catch (Exception e) {
            Map<String, Object> database = new HashMap<>();
            database.put("status", "DOWN");
            database.put("error", e.getMessage());
            health.put("database", database);
        }
        
        return ResponseEntity.ok(health);
    }
} 