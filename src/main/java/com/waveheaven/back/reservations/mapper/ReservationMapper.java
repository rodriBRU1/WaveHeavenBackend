package com.waveheaven.back.reservations.mapper;

import com.waveheaven.back.reservations.dto.ReservationResponse;
import com.waveheaven.back.reservations.entity.Reservation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReservationMapper {

    public ReservationResponse toResponse(Reservation reservation) {
        String productImageUrl = null;
        if (reservation.getProduct().getImages() != null && !reservation.getProduct().getImages().isEmpty()) {
            productImageUrl = reservation.getProduct().getImages().get(0).getUrl();
        }

        return ReservationResponse.builder()
                .id(reservation.getId())
                .userId(reservation.getUser().getId())
                .userEmail(reservation.getUser().getEmail())
                .userFullName(reservation.getUser().getFirstName() + " " + reservation.getUser().getLastName())
                .productId(reservation.getProduct().getId())
                .productName(reservation.getProduct().getName())
                .productImageUrl(productImageUrl)
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .status(reservation.getStatus().name())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }

    public List<ReservationResponse> toResponseList(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
