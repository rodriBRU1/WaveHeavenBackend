package com.waveheaven.back.characteristics.repository;

import com.waveheaven.back.characteristics.entity.Characteristic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacteristicRepository extends JpaRepository<Characteristic, Long> {

    Optional<Characteristic> findByName(String name);

    boolean existsByName(String name);

    List<Characteristic> findByIdIn(List<Long> ids);
}
