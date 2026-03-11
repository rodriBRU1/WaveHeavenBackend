package com.waveheaven.back.reviews.service;

import com.waveheaven.back.auth.entity.User;
import com.waveheaven.back.auth.repository.UserRepository;
import com.waveheaven.back.products.entity.Product;
import com.waveheaven.back.products.repository.ProductRepository;
import com.waveheaven.back.reviews.dto.CreateReviewRequest;
import com.waveheaven.back.reviews.dto.ProductRatingResponse;
import com.waveheaven.back.reviews.dto.ReviewResponse;
import com.waveheaven.back.reviews.entity.Review;
import com.waveheaven.back.reviews.repository.ReviewRepository;
import com.waveheaven.back.shared.exception.BadRequestException;
import com.waveheaven.back.shared.exception.ConflictException;
import com.waveheaven.back.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        // Check if user already reviewed this product
        if (reviewRepository.existsByUserIdAndProductId(user.getId(), request.getProductId())) {
            throw new ConflictException("You have already reviewed this product");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);
        log.info("Review created for product {} by user {}", request.getProductId(), userEmail);

        return toResponse(review);
    }

    @Transactional
    public ReviewResponse updateReview(Long productId, CreateReviewRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Review review = reviewRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);
        log.info("Review updated for product {} by user {}", productId, userEmail);

        return toResponse(review);
    }

    @Transactional
    public void deleteReview(Long productId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!reviewRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new ResourceNotFoundException("Review not found");
        }

        reviewRepository.deleteByUserIdAndProductId(user.getId(), productId);
        log.info("Review deleted for product {} by user {}", productId, userEmail);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getProductReviews(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        List<Review> reviews = reviewRepository.findByProductIdWithUser(productId);
        return reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductRatingResponse getProductRating(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        Double avgRating = reviewRepository.getAverageRatingByProductId(productId);
        long totalReviews = reviewRepository.countByProductId(productId);

        return ProductRatingResponse.builder()
                .productId(productId)
                .averageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0)
                .totalReviews(totalReviews)
                .build();
    }

    @Transactional(readOnly = true)
    public ReviewResponse getUserReviewForProduct(Long productId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return reviewRepository.findByUserIdAndProductId(user.getId(), productId)
                .map(this::toResponse)
                .orElse(null);
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userFullName(review.getUser().getFirstName() + " " + review.getUser().getLastName())
                .userInitials(review.getUser().getInitials())
                .productId(review.getProduct().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
