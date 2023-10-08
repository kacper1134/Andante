package org.andante.speakers.repository;

import org.andante.speakers.repository.entity.SpeakersVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SpeakersVariantRepository extends JpaRepository<SpeakersVariantEntity, Long> {
    Set<SpeakersVariantEntity> findAllBySpeakersId(Long speakersId);
}
