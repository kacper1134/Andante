package org.andante.microphones.repository;

import org.andante.microphones.repository.entity.MicrophonesVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MicrophonesVariantRepository extends JpaRepository<MicrophonesVariantEntity, Long> {
    Set<MicrophonesVariantEntity> findAllByMicrophonesId(Long microphonesId);
}
