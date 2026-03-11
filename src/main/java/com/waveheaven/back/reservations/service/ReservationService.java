package com.waveheaven.back.reservations.service;

import com.waveheaven.back.auth.entity.User;
import com.waveheaven.back.auth.repository.UserRepository;
import com.waveheaven.back.email.service.EmailService;
import com.waveheaven.back.products.entity.Product;
import com.waveheaven.back.products.repository.ProductRepository;
import com.waveheaven.back.reservations.dto.CreateReservationRequest;
import com.waveheaven.back.reservations.dto.ReservationResponse;
import com.waveheaven.back.reservations.entity.Reservation;
import com.waveheaven.back.reservations.entity.ReservationStatus;
import com.waveheaven.back.reservations.mapper.ReservationMapper;
import com.waveheaven.back.reservations.repository.ReservationRepository;
import com.waveheaven.back.shared.exception.BadRequestException;
import com.waveheaven.back.shared.exception.ConflictException;
import com.waveheaven.back.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;
    private final EmailService emailService;

    private static final List<ReservationStatus> BLOCKING_STATUSES = List.of(
            ReservationStatus.PENDING,
            ReservationStatus.CONFIRMED
    );

    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request, String userEmail) {
        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        // Find user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Find product
        Product product = productRepository.findByIdWithImages(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        // Check availability
        boolean hasOverlap = reservationRepository.existsOverlappingReservation(
                request.getProductId(),
                request.getStartDate(),
                request.getEndDate(),
                BLOCKING_STATUSES
        );

        if (hasOverlap) {
            throw new ConflictException("Product is not available for the selected dates");
        }

        // Create reservation
        Reservation reservation = Reservation.builder()
                .user(user)
                .product(product)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(ReservationStatus.CONFIRMED)
                .build();

        reservation = reservationRepository.save(reservation);
        log.info("Reservation created: {} for product {} by user {}",
                reservation.getId(), product.getName(), userEmail);

        // Send confirmation email
        String productImageUrl = product.getImages().isEmpty() ? null : product.getImages().get(0).getUrl();
        String userFullName = user.getFirstName() + " " + user.getLastName();

        emailService.sendReservationConfirmation(
                userEmail,
                userFullName,
                reservation.getId(),
                product.getName(),
                productImageUrl,
                reservation.getStartDate(),
                reservation.getEndDate()
        );

        return reservationMapper.toResponse(reservation);
    }

    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        return reservationMapper.toResponse(reservation);
    }

    public List<ReservationResponse> getUserReservations(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Reservation> reservations = reservationRepository.findByUserIdOrderByStartDateDesc(user.getId());
        return reservationMapper.toResponseList(reservations);
    }

    public Page<ReservationResponse> getUserReservationsPaginated(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> reservations = reservationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        return reservations.map(reservationMapper::toResponse);
    }

    @Transactional
    public ReservationResponse cancelReservation(Long id, String userEmail) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

        // Verify ownership
        if (!reservation.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("You can only cancel your own reservations");
        }

        // Check if can be cancelled
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BadRequestException("Reservation is already cancelled");
        }

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed reservation");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation = reservationRepository.save(reservation);

        log.info("Reservation {} cancelled by user {}", id, userEmail);

        return reservationMapper.toResponse(reservation);
    }

    // Method for checking product availability (used by search)
    public boolean isProductAvailable(Long productId, LocalDate startDate, LocalDate endDate) {
        return !reservationRepository.existsOverlappingReservation(
                productId,
                startDate,
                endDate,
                BLOCKING_STATUSES
        );
    }

    // Get occupied dates for a product (for calendar display)
    public List<ReservationResponse> getProductReservations(Long productId) {
        // Verify product exists
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        LocalDate today = LocalDate.now();
        List<Reservation> reservations = reservationRepository.findReservationsByProductAndDateRange(
                productId,
                today,
                BLOCKING_STATUSES
        );

        return reservationMapper.toResponseList(reservations);
    }
}
