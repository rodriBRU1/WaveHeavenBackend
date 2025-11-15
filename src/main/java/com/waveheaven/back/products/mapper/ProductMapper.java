package com.waveheaven.back.products.mapper;

import com.waveheaven.back.products.dto.CreateProductRequest;
import com.waveheaven.back.products.dto.ImageDTO;
import com.waveheaven.back.products.dto.ProductResponse;
import com.waveheaven.back.products.entity.Image;
import com.waveheaven.back.products.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        // Crear y asociar imágenes
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<Image> images = request.getImages().stream()
                    .map(imageDTO -> Image.builder()
                            .url(imageDTO.getUrl())
                            .altText(imageDTO.getAltText())
                            .product(product)
                            .build())
                    .collect(Collectors.toList());
            product.setImages(images);
        }

        return product;
    }

    public ProductResponse toResponse(Product product) {
        List<ImageDTO> imageDTOs = product.getImages().stream()
                .map(this::toImageDTO)
                .collect(Collectors.toList());

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .images(imageDTOs)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public ImageDTO toImageDTO(Image image) {
        return ImageDTO.builder()
                .id(image.getId())
                .url(image.getUrl())
                .altText(image.getAltText())
                .build();
    }

    public List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
