package com.waveheaven.back.reviews.repository;

import com.waveheaven.back.reviews.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Check if user already reviewed the product
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // Find review by user and product
    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);

    // Get all reviews for a product
    @Query("SELECT r FROM Review r " +
           "JOIN FETCH r.user " +
           "WHERE r.product.id = :productId " +
           "ORDER BY r.createdAt DESC")
    List<Review> findByProductIdWithUser(@Param("productId") Long productId);

    // Get reviews with pagination
    @Query("SELECT r FROM Review r " +
           "JOIN FETCH r.user " +
           "WHERE r.product.id = :productId")
    Page<Review> findByProductIdWithUser(@Param("productId") Long productId, Pageable pageable);

    // Get average rating for a product
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    // Count reviews for a product
    long countByProductId(Long productId);

    // Delete review by user and product
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
