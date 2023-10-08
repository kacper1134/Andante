package org.andante.subwoofers.repository;

import org.andante.subwoofers.repository.entity.SubwoofersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SubwoofersRepository extends JpaRepository<SubwoofersEntity, Long>, JpaSpecificationExecutor<SubwoofersEntity> {
    @Query("SELECT s.id FROM SubwoofersEntity s LEFT JOIN s.comments c GROUP BY s.id HAVING COALESCE(AVG(c.rating),0) >= ?1")
    Set<Long> findByMinimumRating(Double minimumRating);
}
