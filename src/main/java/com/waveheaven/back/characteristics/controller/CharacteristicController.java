package com.waveheaven.back.characteristics.controller;

import com.waveheaven.back.characteristics.dto.CharacteristicResponse;
import com.waveheaven.back.characteristics.dto.CreateCharacteristicRequest;
import com.waveheaven.back.characteristics.dto.UpdateCharacteristicRequest;
import com.waveheaven.back.characteristics.service.CharacteristicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/characteristics")
@RequiredArgsConstructor
@Tag(name = "Characteristics", description = "Gestión de características de productos")
public class CharacteristicController {

    private final CharacteristicService characteristicService;

    @PostMapping
    @Operation(summary = "Crear característica", description = "Crea una nueva característica con nombre e icono (Solo Admin)")
    public ResponseEntity<CharacteristicResponse> createCharacteristic(@Valid @RequestBody CreateCharacteristicRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(characteristicService.createCharacteristic(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener característica por ID", description = "Retorna una característica específica")
    public ResponseEntity<CharacteristicResponse> getCharacteristicById(@PathVariable Long id) {
        return ResponseEntity.ok(characteristicService.getCharacteristicById(id));
    }

    @GetMapping
    @Operation(summary = "Listar características", description = "Retorna todas las características disponibles")
    public ResponseEntity<List<CharacteristicResponse>> getAllCharacteristics() {
        return ResponseEntity.ok(characteristicService.getAllCharacteristics());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar característica", description = "Actualiza una característica existente (Solo Admin)")
    public ResponseEntity<CharacteristicResponse> updateCharacteristic(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCharacteristicRequest request
    ) {
        return ResponseEntity.ok(characteristicService.updateCharacteristic(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar característica", description = "Elimina una característica por ID (Solo Admin)")
    public ResponseEntity<Void> deleteCharacteristic(@PathVariable Long id) {
        characteristicService.deleteCharacteristic(id);
        return ResponseEntity.noContent().build();
    }
}
