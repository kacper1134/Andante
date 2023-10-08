package org.andante.speakers.repository;

import org.andante.speakers.repository.entity.SpeakersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SpeakersRepository extends JpaRepository<SpeakersEntity, Long>, JpaSpecificationExecutor<SpeakersEntity> {

    @Query("SELECT s.id FROM SpeakersEntity s LEFT JOIN s.comments c GROUP BY s.id HAVING COALESCE(AVG(c.rating),0) >= ?1")
    Set<Long> findByMinimumRating(Double minimumRating);
}
