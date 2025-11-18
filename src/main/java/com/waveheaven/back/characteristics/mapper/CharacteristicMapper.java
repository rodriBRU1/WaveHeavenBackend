package com.waveheaven.back.characteristics.mapper;

import com.waveheaven.back.characteristics.dto.CharacteristicResponse;
import com.waveheaven.back.characteristics.dto.CreateCharacteristicRequest;
import com.waveheaven.back.characteristics.entity.Characteristic;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CharacteristicMapper {

    public Characteristic toEntity(CreateCharacteristicRequest request) {
        return Characteristic.builder()
                .name(request.getName())
                .iconUrl(request.getIconUrl())
                .build();
    }

    public CharacteristicResponse toResponse(Characteristic characteristic) {
        return CharacteristicResponse.builder()
                .id(characteristic.getId())
                .name(characteristic.getName())
                .iconUrl(characteristic.getIconUrl())
                .createdAt(characteristic.getCreatedAt())
                .updatedAt(characteristic.getUpdatedAt())
                .build();
    }

    public List<CharacteristicResponse> toResponseList(List<Characteristic> characteristics) {
        return characteristics.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
