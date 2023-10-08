package org.andante.headphones.repository;

import org.andante.headphones.repository.entity.HeadphonesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface HeadphonesRepository extends JpaRepository<HeadphonesEntity, Long>, JpaSpecificationExecutor<HeadphonesEntity> {

    @Query("SELECT h.id FROM HeadphonesEntity h LEFT JOIN h.comments c GROUP BY h.id HAVING COALESCE(AVG(c.rating),0) >= ?1")
    Set<Long> findByMinimumRating(Double minimumRating);
}
