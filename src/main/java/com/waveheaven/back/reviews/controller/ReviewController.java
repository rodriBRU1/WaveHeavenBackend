package com.waveheaven.back.reviews.controller;

import com.waveheaven.back.reviews.dto.CreateReviewRequest;
import com.waveheaven.back.reviews.dto.ProductRatingResponse;
import com.waveheaven.back.reviews.dto.ReviewResponse;
import com.waveheaven.back.reviews.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Product reviews and ratings endpoints")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Create a new review")
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewResponse response = reviewService.createReview(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/product/{productId}")
    @Operation(summary = "Update your review for a product")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewResponse response = reviewService.updateReview(productId, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/product/{productId}")
    @Operation(summary = "Delete your review for a product")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.deleteReview(productId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get all reviews for a product")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    @GetMapping("/product/{productId}/rating")
    @Operation(summary = "Get average rating and total reviews for a product")
    public ResponseEntity<ProductRatingResponse> getProductRating(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductRating(productId));
    }

    @GetMapping("/product/{productId}/my-review")
    @Operation(summary = "Get current user's review for a product")
    public ResponseEntity<ReviewResponse> getMyReview(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewResponse response = reviewService.getUserReviewForProduct(productId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
