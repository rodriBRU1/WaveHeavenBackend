package com.waveheaven.back.categories.service;

import com.waveheaven.back.categories.dto.CategoryResponse;
import com.waveheaven.back.categories.dto.CreateCategoryRequest;
import com.waveheaven.back.categories.dto.UpdateCategoryRequest;
import com.waveheaven.back.categories.entity.Category;
import com.waveheaven.back.categories.mapper.CategoryMapper;
import com.waveheaven.back.categories.repository.CategoryRepository;
import com.waveheaven.back.shared.exception.ConflictException;
import com.waveheaven.back.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByTitle(request.getTitle())) {
            throw new ConflictException("Ya existe una categoría con el título: " + request.getTitle());
        }

        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);
        log.info("Categoría creada con ID: {}", savedCategory.getId());

        return categoryMapper.toResponse(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        return categoryMapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toResponseList(categories);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        if (request.getTitle() != null && !request.getTitle().equals(category.getTitle())) {
            if (categoryRepository.existsByTitle(request.getTitle())) {
                throw new ConflictException("Ya existe una categoría con el título: " + request.getTitle());
            }
            category.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        if (request.getImageUrl() != null) {
            category.setImageUrl(request.getImageUrl());
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("Categoría actualizada con ID: {}", updatedCategory.getId());

        return categoryMapper.toResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }

        categoryRepository.deleteById(id);
        log.info("Categoría eliminada con ID: {}", id);
    }
}
