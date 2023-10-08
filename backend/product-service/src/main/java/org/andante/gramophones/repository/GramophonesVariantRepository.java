package org.andante.gramophones.repository;

import org.andante.gramophones.repository.entity.GramophonesVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GramophonesVariantRepository extends JpaRepository<GramophonesVariantEntity, Long> {
    Set<GramophonesVariantEntity> findAllByGramophonesId(Long gramophonesId);
}
