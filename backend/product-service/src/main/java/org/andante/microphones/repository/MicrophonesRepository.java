package org.andante.microphones.repository;

import org.andante.microphones.repository.entity.MicrophonesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MicrophonesRepository extends JpaRepository<MicrophonesEntity, Long>, JpaSpecificationExecutor<MicrophonesEntity> {

    @Query("SELECT m.id FROM MicrophonesEntity m LEFT JOIN m.comments c GROUP BY m.id HAVING COALESCE(AVG(c.rating),0) >= ?1")
    Set<Long> findByMinimumRating(Double minimumRating);
}
