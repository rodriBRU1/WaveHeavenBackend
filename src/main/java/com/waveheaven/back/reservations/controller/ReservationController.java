package com.waveheaven.back.reservations.controller;

import com.waveheaven.back.reservations.dto.CreateReservationRequest;
import com.waveheaven.back.reservations.dto.ReservationResponse;
import com.waveheaven.back.reservations.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Reservation management endpoints")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create a new reservation")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReservationResponse response = reservationService.createReservation(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping("/my-reservations")
    @Operation(summary = "Get current user's reservations")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reservationService.getUserReservations(userDetails.getUsername()));
    }

    @GetMapping("/my-reservations/paginated")
    @Operation(summary = "Get current user's reservations with pagination")
    public ResponseEntity<Page<ReservationResponse>> getMyReservationsPaginated(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reservationService.getUserReservationsPaginated(
                userDetails.getUsername(), page, size));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a reservation")
    public ResponseEntity<ReservationResponse> cancelReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reservationService.cancelReservation(id, userDetails.getUsername()));
    }

    @GetMapping("/product/{productId}/availability")
    @Operation(summary = "Get product reservations for availability calendar")
    public ResponseEntity<List<ReservationResponse>> getProductAvailability(@PathVariable Long productId) {
        return ResponseEntity.ok(reservationService.getProductReservations(productId));
    }
}
