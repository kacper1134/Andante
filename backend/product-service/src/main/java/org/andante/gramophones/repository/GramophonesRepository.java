package org.andante.gramophones.repository;

import org.andante.gramophones.repository.entity.GramophonesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GramophonesRepository extends JpaRepository<GramophonesEntity, Long>, JpaSpecificationExecutor<GramophonesEntity> {
    @Query("SELECT g.id FROM GramophonesEntity g LEFT JOIN g.comments c GROUP BY g.id HAVING COALESCE(AVG(c.rating),0) >= ?1")
    Set<Long> findByMinimumRating(Double minimumRating);
}
