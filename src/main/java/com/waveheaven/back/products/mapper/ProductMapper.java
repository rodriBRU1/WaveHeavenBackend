package com.waveheaven.back.products.mapper;

import com.waveheaven.back.characteristics.dto.CharacteristicResponse;
import com.waveheaven.back.characteristics.entity.Characteristic;
import com.waveheaven.back.products.dto.CreateProductRequest;
import com.waveheaven.back.products.dto.ImageDTO;
import com.waveheaven.back.products.dto.ProductResponse;
import com.waveheaven.back.products.entity.Image;
import com.waveheaven.back.products.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Collections;
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

        List<CharacteristicResponse> characteristicDTOs = product.getCharacteristics() != null
                ? product.getCharacteristics().stream()
                    .map(this::toCharacteristicResponse)
                    .collect(Collectors.toList())
                : Collections.emptyList();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryTitle(product.getCategory() != null ? product.getCategory().getTitle() : null)
                .characteristics(characteristicDTOs)
                .images(imageDTOs)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private CharacteristicResponse toCharacteristicResponse(Characteristic characteristic) {
        return CharacteristicResponse.builder()
                .id(characteristic.getId())
                .name(characteristic.getName())
                .iconUrl(characteristic.getIconUrl())
                .createdAt(characteristic.getCreatedAt())
                .updatedAt(characteristic.getUpdatedAt())
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
