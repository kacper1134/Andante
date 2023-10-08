package org.andante.headphones.repository;

import org.andante.headphones.repository.entity.HeadphonesVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface HeadphonesVariantRepository extends JpaRepository<HeadphonesVariantEntity, Long> {

    Set<HeadphonesVariantEntity> findAllByHeadphonesId(Long headpgonesId);
}
