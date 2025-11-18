package com.waveheaven.back.characteristics.service;

import com.waveheaven.back.characteristics.dto.CharacteristicResponse;
import com.waveheaven.back.characteristics.dto.CreateCharacteristicRequest;
import com.waveheaven.back.characteristics.dto.UpdateCharacteristicRequest;
import com.waveheaven.back.characteristics.entity.Characteristic;
import com.waveheaven.back.characteristics.mapper.CharacteristicMapper;
import com.waveheaven.back.characteristics.repository.CharacteristicRepository;
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
public class CharacteristicService {

    private final CharacteristicRepository characteristicRepository;
    private final CharacteristicMapper characteristicMapper;

    @Transactional
    public CharacteristicResponse createCharacteristic(CreateCharacteristicRequest request) {
        if (characteristicRepository.existsByName(request.getName())) {
            throw new ConflictException("Ya existe una característica con el nombre: " + request.getName());
        }

        Characteristic characteristic = characteristicMapper.toEntity(request);
        Characteristic savedCharacteristic = characteristicRepository.save(characteristic);
        log.info("Característica creada con ID: {}", savedCharacteristic.getId());

        return characteristicMapper.toResponse(savedCharacteristic);
    }

    @Transactional(readOnly = true)
    public CharacteristicResponse getCharacteristicById(Long id) {
        Characteristic characteristic = characteristicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada con ID: " + id));

        return characteristicMapper.toResponse(characteristic);
    }

    @Transactional(readOnly = true)
    public List<CharacteristicResponse> getAllCharacteristics() {
        List<Characteristic> characteristics = characteristicRepository.findAll();
        return characteristicMapper.toResponseList(characteristics);
    }

    @Transactional
    public CharacteristicResponse updateCharacteristic(Long id, UpdateCharacteristicRequest request) {
        Characteristic characteristic = characteristicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Característica no encontrada con ID: " + id));

        if (request.getName() != null && !request.getName().equals(characteristic.getName())) {
            if (characteristicRepository.existsByName(request.getName())) {
                throw new ConflictException("Ya existe una característica con el nombre: " + request.getName());
            }
            characteristic.setName(request.getName());
        }

        if (request.getIconUrl() != null) {
            characteristic.setIconUrl(request.getIconUrl());
        }

        Characteristic updatedCharacteristic = characteristicRepository.save(characteristic);
        log.info("Característica actualizada con ID: {}", updatedCharacteristic.getId());

        return characteristicMapper.toResponse(updatedCharacteristic);
    }

    @Transactional
    public void deleteCharacteristic(Long id) {
        if (!characteristicRepository.existsById(id)) {
            throw new ResourceNotFoundException("Característica no encontrada con ID: " + id);
        }

        characteristicRepository.deleteById(id);
        log.info("Característica eliminada con ID: {}", id);
    }
}
