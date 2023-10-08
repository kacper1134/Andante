package org.andante.amplifiers.repository;

import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AmplifiersRepository extends JpaRepository<AmplifiersEntity, Long>, JpaSpecificationExecutor<AmplifiersEntity> {
    @Query("SELECT a.id FROM AmplifiersEntity a LEFT JOIN a.comments c GROUP BY a.id HAVING COALESCE(AVG(c.rating),0) >= ?1")
    Set<Long> findByMinimumRating(Double minimumRating);
}
