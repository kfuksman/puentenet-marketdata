package com.puentenet.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.puentenet.domain.dto.UserListResponse;
import com.puentenet.domain.User;
import com.puentenet.service.InstrumentService;
import com.puentenet.service.MarketDataScheduler;
import com.puentenet.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Administrative APIs")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private InstrumentService instrumentService;

    @Autowired
    private MarketDataScheduler marketDataScheduler;

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    @Operation(summary = "List all users", description = "Retrieve list of all registered users (Admin only)")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<UserListResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserListResponse> userResponses = users.stream()
                .map(user -> new UserListResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }

    @PostMapping("/update-market-data")
    @Operation(summary = "Manual market data update", description = "Trigger manual update of market data")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> updateMarketData() {
        try {
            marketDataScheduler.updateMarketData();
            return ResponseEntity.ok("Market data update completed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating market data: " + e.getMessage());
        }
    }

    @PostMapping("/initialize-instruments")
    @Operation(summary = "Initialize instruments", description = "Initialize instruments database with market data")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> initializeInstruments() {
        try {
            marketDataScheduler.initializeInstruments();
            return ResponseEntity.ok("Instruments initialized successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error initializing instruments: " + e.getMessage());
        }
    }
} 