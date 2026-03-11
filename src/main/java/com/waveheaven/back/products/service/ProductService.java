package com.waveheaven.back.products.service;

import com.waveheaven.back.categories.entity.Category;
import com.waveheaven.back.categories.repository.CategoryRepository;
import com.waveheaven.back.characteristics.entity.Characteristic;
import com.waveheaven.back.characteristics.repository.CharacteristicRepository;
import com.waveheaven.back.products.dto.CreateProductRequest;
import com.waveheaven.back.products.dto.ProductResponse;
import com.waveheaven.back.products.dto.UpdateProductRequest;
import com.waveheaven.back.products.entity.Image;
import com.waveheaven.back.products.entity.Policy;
import com.waveheaven.back.products.entity.Product;
import com.waveheaven.back.products.mapper.ProductMapper;
import com.waveheaven.back.products.repository.ProductRepository;
import com.waveheaven.back.reservations.entity.ReservationStatus;
import com.waveheaven.back.reservations.repository.ReservationRepository;
import com.waveheaven.back.shared.exception.ConflictException;
import com.waveheaven.back.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl; // Importante: Nuevo import
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList; // Importante: Nuevo import
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
    private final ReservationRepository reservationRepository;
    private final ProductMapper productMapper;
    private final Random random = new Random();

    private static final List<ReservationStatus> BLOCKING_STATUSES = List.of(
            ReservationStatus.PENDING,
            ReservationStatus.CONFIRMED
    );

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating product with name: {}", request.getName());

        if (productRepository.existsByName(request.getName())) {
            throw new ConflictException("A product with the name '" + request.getName() + "' already exists");
        }

        Product product = productMapper.toEntity(request);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));
            product.setCategory(category);
        }

        if (request.getCharacteristicIds() != null && !request.getCharacteristicIds().isEmpty()) {
            List<Characteristic> characteristics = characteristicRepository.findByIdIn(request.getCharacteristicIds());
            product.setCharacteristics(characteristics);
        }

        if (request.getPolicies() != null && !request.getPolicies().isEmpty()) {
            request.getPolicies().forEach(policyDTO -> {
                Policy policy = Policy.builder()
                        .title(policyDTO.getTitle())
                        .description(policyDTO.getDescription())
                        .product(product)
                        .build();
                product.addPolicy(policy);
            });
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

    // --- MÉTODO MODIFICADO PARA MOSTRAR SIEMPRE 40 PRODUCTOS ---
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(int page, int size) {
        log.info("Fetching all products (inflated to 40) - page: {}, size: {}", page, size);

        // 1. Obtener todos los productos reales ordenados por ID
        List<Product> realProducts = productRepository.findAll(Sort.by("id").ascending());

        // Convertirlos a DTO
        List<ProductResponse> realDtos = realProducts.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());

        // Si no hay productos reales, retornamos vacío (necesitas al menos 1 para clonar)
        if (realDtos.isEmpty()) {
            return Page.empty();
        }

        // 2. Crear una lista nueva e inflarla hasta 40 items
        List<ProductResponse> allProducts = new ArrayList<>(realDtos);
        
        long fakeId = 10000L; // ID inicial alto para las copias (evita colisión con reales)

        while (allProducts.size() < 40) {
            // Recorremos los originales y vamos creando copias
            for (ProductResponse original : realDtos) {
                if (allProducts.size() >= 40) break;

                // Creamos un clon con un ID falso
                ProductResponse copy = ProductResponse.builder()
                        .id(fakeId++) // ID Falso único
                        .name(original.getName()) 
                        .description(original.getDescription())
                        .categoryId(original.getCategoryId())
                        .categoryTitle(original.getCategoryTitle())
                        .characteristics(original.getCharacteristics())
                        .images(original.getImages())
                        .policies(original.getPolicies())
                        .createdAt(original.getCreatedAt())
                        .updatedAt(original.getUpdatedAt())
                        .build();

                allProducts.add(copy);
            }
        }

        // 3. Paginación Manual sobre la lista de 40 items
        int start = page * size;
        int end = Math.min((start + size), allProducts.size());
        
        List<ProductResponse> pageContent;
        if (start >= allProducts.size()) {
            pageContent = Collections.emptyList();
        } else {
            pageContent = allProducts.subList(start, end);
        }

        // Retornamos la página construida manualmente
        return new PageImpl<>(pageContent, PageRequest.of(page, size), allProducts.size());
    }
    // -----------------------------------------------------------

    @Transactional(readOnly = true)
    public List<ProductResponse> getRandomProducts(int count) {
        log.info("Fetching {} random products", count);

        List<Product> allProducts = productRepository.findAll();

        if (allProducts.isEmpty()) {
            return Collections.emptyList();
        }

        int maxCount = Math.min(count, 10);
        maxCount = Math.min(maxCount, allProducts.size());

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


        if (request.getName() != null && !request.getName().equals(product.getName())) {
            if (productRepository.existsByName(request.getName())) {
                throw new ConflictException("A product with the name '" + request.getName() + "' already exists");
            }
            product.setName(request.getName());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));
            product.setCategory(category);
        }

        if (request.getCharacteristicIds() != null) {
            List<Characteristic> characteristics = characteristicRepository.findByIdIn(request.getCharacteristicIds());
            product.setCharacteristics(characteristics);
        }

        if (request.getImages() != null) {
            product.getImages().clear();
            request.getImages().forEach(imageDTO -> {
                Image image = Image.builder()
                        .url(imageDTO.getUrl())
                        .product(product)
                        .build();
                product.addImage(image);
            });
        }

        if (request.getPolicies() != null) {
            product.getPolicies().clear();
            request.getPolicies().forEach(policyDTO -> {
                Policy policy = Policy.builder()
                        .title(policyDTO.getTitle())
                        .description(policyDTO.getDescription())
                        .product(product)
                        .build();
                product.addPolicy(policy);
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

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with ID: " + categoryId);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);

        return productPage.map(productMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(
            String name,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {

        log.info("Searching products - name: {}, categoryId: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                name, categoryId, startDate, endDate, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage;

        List<Long> unavailableProductIds = Collections.emptyList();
        if (startDate != null && endDate != null) {
            unavailableProductIds = getUnavailableProductIds(startDate, endDate);
        }

        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasCategory = categoryId != null;
        boolean hasDateFilter = !unavailableProductIds.isEmpty();

        if (hasDateFilter) {
            if (hasName && hasCategory) {
                productPage = productRepository.searchByNameAndCategoryExcludingIds(
                        name.trim(), categoryId, unavailableProductIds, pageable);
            } else if (hasName) {
                productPage = productRepository.searchByNameExcludingIds(
                        name.trim(), unavailableProductIds, pageable);
            } else if (hasCategory) {
                productPage = productRepository.searchByCategoryExcludingIds(
                        categoryId, unavailableProductIds, pageable);
            } else {
                productPage = productRepository.findByIdNotIn(unavailableProductIds, pageable);
            }
        } else {
            if (hasName && hasCategory) {
                productPage = productRepository.searchByNameAndCategory(
                        name.trim(), categoryId, pageable);
            } else if (hasName) {
                productPage = productRepository.searchByName(name.trim(), pageable);
            } else if (hasCategory) {
                productPage = productRepository.searchByCategory(categoryId, pageable);
            } else {
                productPage = productRepository.findAllWithImages(pageable);
            }
        }

        return productPage.map(productMapper::toResponse);
    }

    private List<Long> getUnavailableProductIds(LocalDate startDate, LocalDate endDate) {
        return productRepository.findAll().stream()
                .filter(product -> reservationRepository.existsOverlappingReservation(
                        product.getId(), startDate, endDate, BLOCKING_STATUSES))
                .map(Product::getId)
                .collect(Collectors.toList());
    }
}   