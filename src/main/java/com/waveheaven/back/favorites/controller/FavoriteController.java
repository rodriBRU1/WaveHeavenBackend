package com.waveheaven.back.favorites.controller;

import com.waveheaven.back.favorites.dto.FavoriteResponse;
import com.waveheaven.back.favorites.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Favorite products management endpoints")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{productId}")
    @Operation(summary = "Add product to favorites")
    public ResponseEntity<FavoriteResponse> addFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        FavoriteResponse response = favoriteService.addFavorite(productId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove product from favorites")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        favoriteService.removeFavorite(productId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all user favorites")
    public ResponseEntity<List<FavoriteResponse>> getUserFavorites(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(userDetails.getUsername()));
    }

    @GetMapping("/{productId}/check")
    @Operation(summary = "Check if product is in favorites")
    public ResponseEntity<Map<String, Boolean>> isFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean isFav = favoriteService.isFavorite(productId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("isFavorite", isFav));
    }

    @GetMapping("/ids")
    @Operation(summary = "Get all favorite product IDs for current user")
    public ResponseEntity<List<Long>> getFavoriteProductIds(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(favoriteService.getUserFavoriteProductIds(userDetails.getUsername()));
    }
}
