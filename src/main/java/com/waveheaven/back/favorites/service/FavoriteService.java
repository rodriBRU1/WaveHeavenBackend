package com.waveheaven.back.favorites.service;

import com.waveheaven.back.auth.entity.User;
import com.waveheaven.back.auth.repository.UserRepository;
import com.waveheaven.back.favorites.dto.FavoriteResponse;
import com.waveheaven.back.favorites.entity.Favorite;
import com.waveheaven.back.favorites.repository.FavoriteRepository;
import com.waveheaven.back.products.entity.Product;
import com.waveheaven.back.products.mapper.ProductMapper;
import com.waveheaven.back.products.repository.ProductRepository;
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
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public FavoriteResponse addFavorite(Long productId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findByIdWithImages(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Check if already favorited
        if (favoriteRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new ConflictException("Product is already in favorites");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .product(product)
                .build();

        favorite = favoriteRepository.save(favorite);
        log.info("Product {} added to favorites by user {}", productId, userEmail);

        return toResponse(favorite);
    }

    @Transactional
    public void removeFavorite(Long productId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!favoriteRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new ResourceNotFoundException("Product is not in favorites");
        }

        favoriteRepository.deleteByUserIdAndProductId(user.getId(), productId);
        log.info("Product {} removed from favorites by user {}", productId, userEmail);
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> getUserFavorites(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Favorite> favorites = favoriteRepository.findByUserIdWithProductAndImages(user.getId());

        return favorites.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(Long productId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return favoriteRepository.existsByUserIdAndProductId(user.getId(), productId);
    }

    @Transactional(readOnly = true)
    public List<Long> getUserFavoriteProductIds(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return favoriteRepository.findProductIdsByUserId(user.getId());
    }

    private FavoriteResponse toResponse(Favorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .product(productMapper.toResponse(favorite.getProduct()))
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
