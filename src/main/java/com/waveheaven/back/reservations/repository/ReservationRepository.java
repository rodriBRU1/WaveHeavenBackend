package com.waveheaven.back.reservations.repository;

import com.waveheaven.back.reservations.entity.Reservation;
import com.waveheaven.back.reservations.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Find reservations by user
    Page<Reservation> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Reservation> findByUserIdOrderByStartDateDesc(Long userId);

    // Find reservations by product
    List<Reservation> findByProductIdAndStatusIn(Long productId, List<ReservationStatus> statuses);

    // Check for overlapping reservations (for availability)
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
           "WHERE r.product.id = :productId " +
           "AND r.status IN :statuses " +
           "AND r.startDate <= :endDate " +
           "AND r.endDate >= :startDate")
    boolean existsOverlappingReservation(
            @Param("productId") Long productId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") List<ReservationStatus> statuses);

    // Get all reserved date ranges for a product (for calendar display)
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.product.id = :productId " +
           "AND r.status IN :statuses " +
           "AND r.endDate >= :fromDate " +
           "ORDER BY r.startDate")
    List<Reservation> findReservationsByProductAndDateRange(
            @Param("productId") Long productId,
            @Param("fromDate") LocalDate fromDate,
            @Param("statuses") List<ReservationStatus> statuses);

    // Find products that are available in a date range (for search)
    @Query("SELECT DISTINCT p.id FROM Reservation r " +
           "RIGHT JOIN r.product p " +
           "WHERE r.id IS NULL " +
           "OR NOT (r.status IN :statuses " +
           "AND r.startDate <= :endDate " +
           "AND r.endDate >= :startDate)")
    List<Long> findAvailableProductIds(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") List<ReservationStatus> statuses);
}
