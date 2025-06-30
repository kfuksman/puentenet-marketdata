package com.puentenet.controller;

import com.puentenet.domain.dto.InstrumentResponse;
import com.puentenet.domain.User;
import com.puentenet.service.InstrumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instruments")
@Tag(name = "Instruments", description = "Market instruments management APIs")
@CrossOrigin(origins = "*")
public class InstrumentController {

    @Autowired
    private InstrumentService instrumentService;

    @GetMapping
    @Operation(summary = "Get all instruments", description = "Retrieve list of all available instruments")
    public ResponseEntity<List<InstrumentResponse>> getAllInstruments(@AuthenticationPrincipal User user) {
        List<InstrumentResponse> instruments = instrumentService.getAllInstruments(user);
        return ResponseEntity.ok(instruments);
    }

    @GetMapping("/search")
    @Operation(summary = "Search instruments", description = "Search instruments by symbol or name")
    public ResponseEntity<List<InstrumentResponse>> searchInstruments(
            @RequestParam String q,
            @AuthenticationPrincipal User user) {
        List<InstrumentResponse> instruments = instrumentService.searchInstruments(q, user);
        return ResponseEntity.ok(instruments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get instrument by ID", description = "Retrieve detailed information about a specific instrument")
    public ResponseEntity<InstrumentResponse> getInstrumentById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            InstrumentResponse instrument = instrumentService.getInstrumentById(id, user);
            return ResponseEntity.ok(instrument);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/favorites")
    @Operation(summary = "Get user favorites", description = "Retrieve user's favorite instruments")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<InstrumentResponse>> getUserFavorites(@AuthenticationPrincipal User user) {
        List<InstrumentResponse> favorites = instrumentService.getUserFavorites(user);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/{id}/favorite")
    @Operation(summary = "Add to favorites", description = "Add an instrument to user's favorites")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> addToFavorites(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            instrumentService.addToFavorites(id, user);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/favorite")
    @Operation(summary = "Remove from favorites", description = "Remove an instrument from user's favorites")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> removeFromFavorites(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            instrumentService.removeFromFavorites(id, user);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 