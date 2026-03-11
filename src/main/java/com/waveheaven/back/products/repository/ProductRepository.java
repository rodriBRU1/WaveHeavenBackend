package com.waveheaven.back.products.repository;

import com.waveheaven.back.products.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images")
    Page<Product> findAllWithImages(Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(Long id);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.category.id = :categoryId")
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countByCategoryId(Long categoryId);

    // Search by name (case-insensitive)
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images " +
           "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Product> searchByName(@Param("name") String name, Pageable pageable);

    // Search by name and category
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images " +
           "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND p.category.id = :categoryId")
    Page<Product> searchByNameAndCategory(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            Pageable pageable);

    // Search by category only
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images " +
           "WHERE p.category.id = :categoryId")
    Page<Product> searchByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    // Find all products with images (for filtering by availability)
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images " +
           "WHERE p.id IN :productIds")
    Page<Product> findByIdIn(@Param("productIds") List<Long> productIds, Pageable pageable);

    // Find all products not in the given IDs (products with reservations)
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images " +
           "WHERE p.id NOT IN :excludedIds")
    Page<Product> findByIdNotIn(@Param("excludedIds") List<Long> excludedIds, Pageable pageable);

    // Combined search with name filter and excluded IDs
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images " +
           "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND p.id NOT IN :excludedIds")
    Page<Product> searchByNameExcludingIds(
            @Param("name") String name,
            @Param("excludedIds") List<Long> excludedIds,
            Pageable pageable);

    // Combined search with category filter and excluded IDs
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images " +
           "WHERE p.category.id = :categoryId " +
           "AND p.id NOT IN :excludedIds")
    Page<Product> searchByCategoryExcludingIds(
            @Param("categoryId") Long categoryId,
            @Param("excludedIds") List<Long> excludedIds,
            Pageable pageable);

    // Combined search with name, category and excluded IDs
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images " +
           "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND p.category.id = :categoryId " +
           "AND p.id NOT IN :excludedIds")
    Page<Product> searchByNameAndCategoryExcludingIds(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("excludedIds") List<Long> excludedIds,
            Pageable pageable);
}
