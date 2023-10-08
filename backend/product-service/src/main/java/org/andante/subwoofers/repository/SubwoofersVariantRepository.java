package org.andante.subwoofers.repository;

import org.andante.subwoofers.repository.entity.SubwoofersVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SubwoofersVariantRepository extends JpaRepository<SubwoofersVariantEntity, Long> {

    Set<SubwoofersVariantEntity> findAllBySubwoofersId(Long subwoofersId);
}
