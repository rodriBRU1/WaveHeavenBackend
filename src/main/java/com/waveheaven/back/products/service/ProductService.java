package com.waveheaven.back.products.service;

import com.waveheaven.back.categories.entity.Category;
import com.waveheaven.back.categories.repository.CategoryRepository;
import com.waveheaven.back.characteristics.entity.Characteristic;
import com.waveheaven.back.characteristics.repository.CharacteristicRepository;
import com.waveheaven.back.products.dto.CreateProductRequest;
import com.waveheaven.back.products.dto.ProductResponse;
import com.waveheaven.back.products.dto.UpdateProductRequest;
import com.waveheaven.back.products.entity.Image;
import com.waveheaven.back.products.entity.Product;
import com.waveheaven.back.products.mapper.ProductMapper;
import com.waveheaven.back.products.repository.ProductRepository;
import com.waveheaven.back.shared.exception.ConflictException;
import com.waveheaven.back.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CharacteristicRepository characteristicRepository;
    private final ProductMapper productMapper;
    private final Random random = new Random();

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating product with name: {}", request.getName());

        // Verificar si ya existe un producto con ese nombre
        if (productRepository.existsByName(request.getName())) {
            throw new ConflictException("A product with the name '" + request.getName() + "' already exists");
        }

        Product product = productMapper.toEntity(request);

        // Asignar categoría si se proporciona
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));
            product.setCategory(category);
        }

        // Asignar características si se proporcionan
        if (request.getCharacteristicIds() != null && !request.getCharacteristicIds().isEmpty()) {
            List<Characteristic> characteristics = characteristicRepository.findByIdIn(request.getCharacteristicIds());
            product.setCharacteristics(characteristics);
        }

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return productMapper.toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);

        Product product = productRepository.findByIdWithImages(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(int page, int size) {
        log.info("Fetching all products - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findAllWithImages(pageable);

        return productPage.map(productMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getRandomProducts(int count) {
        log.info("Fetching {} random products", count);

        List<Product> allProducts = productRepository.findAll();

        if (allProducts.isEmpty()) {
            return Collections.emptyList();
        }

        // Limitar el conteo a máximo 10 productos como dice el Sprint 1
        int maxCount = Math.min(count, 10);
        maxCount = Math.min(maxCount, allProducts.size());

        // Barajar y tomar los primeros 'maxCount' productos
        Collections.shuffle(allProducts, random);
        List<Product> randomProducts = allProducts.stream()
                .limit(maxCount)
                .collect(Collectors.toList());

        return productMapper.toResponseList(randomProducts);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product with ID: {}", id);

        Product product = productRepository.findByIdWithImages(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        // Verificar si el nuevo nombre ya existe (si se está cambiando el nombre)
        if (request.getName() != null && !request.getName().equals(product.getName())) {
            if (productRepository.existsByName(request.getName())) {
                throw new ConflictException("A product with the name '" + request.getName() + "' already exists");
            }
            product.setName(request.getName());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        // Actualizar categoría si se proporciona
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));
            product.setCategory(category);
        }

        // Actualizar características si se proporcionan
        if (request.getCharacteristicIds() != null) {
            List<Characteristic> characteristics = characteristicRepository.findByIdIn(request.getCharacteristicIds());
            product.setCharacteristics(characteristics);
        }

        // Actualizar imágenes si se proporcionan
        if (request.getImages() != null) {
            product.getImages().clear();
            request.getImages().forEach(imageDTO -> {
                Image image = Image.builder()
                        .url(imageDTO.getUrl())
                        .altText(imageDTO.getAltText())
                        .product(product)
                        .build();
                product.addImage(image);
            });
        }

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully with ID: {}", updatedProduct.getId());
        return productMapper.toResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);

        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }

        productRepository.deleteById(id);
        log.info("Product deleted successfully with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(Long categoryId, int page, int size) {
        log.info("Fetching products by category ID: {} - page: {}, size: {}", categoryId, page, size);

        // Verificar que la categoría existe
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with ID: " + categoryId);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);

        return productPage.map(productMapper::toResponse);
    }
}
