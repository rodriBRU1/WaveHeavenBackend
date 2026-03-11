package com.waveheaven.back.favorites.repository;

import com.waveheaven.back.favorites.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Check if a product is already favorited by user
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // Find favorite by user and product
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);

    // Get all favorites for a user
    @Query("SELECT f FROM Favorite f " +
           "JOIN FETCH f.product p " +
           "LEFT JOIN FETCH p.images " +
           "WHERE f.user.id = :userId " +
           "ORDER BY f.createdAt DESC")
    List<Favorite> findByUserIdWithProductAndImages(@Param("userId") Long userId);

    // Get favorites with pagination
    @Query("SELECT f FROM Favorite f " +
           "JOIN FETCH f.product p " +
           "LEFT JOIN FETCH p.images " +
           "WHERE f.user.id = :userId")
    Page<Favorite> findByUserIdWithProductAndImages(@Param("userId") Long userId, Pageable pageable);

    // Delete by user and product
    void deleteByUserIdAndProductId(Long userId, Long productId);

    // Count favorites for a product
    long countByProductId(Long productId);

    // Get all product IDs favorited by user
    @Query("SELECT f.product.id FROM Favorite f WHERE f.user.id = :userId")
    List<Long> findProductIdsByUserId(@Param("userId") Long userId);
}
