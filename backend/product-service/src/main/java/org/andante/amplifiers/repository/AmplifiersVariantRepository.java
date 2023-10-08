package org.andante.amplifiers.repository;

import org.andante.amplifiers.repository.entity.AmplifiersVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AmplifiersVariantRepository extends JpaRepository<AmplifiersVariantEntity, Long> {
    Set<AmplifiersVariantEntity> findAllByAmplifiersId(Long amplifiersId);
}
