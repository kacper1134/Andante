package org.andante.product.repository;

import org.andante.product.repository.entity.ProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    @Query("SELECT p.id FROM ProductEntity p LEFT JOIN p.comments c GROUP BY p.id ORDER BY COALESCE(AVG(c.rating),0) DESC")
    List<Long> getTopIdsByAverageRating(Pageable pageRequest);
    @Query("SELECT p.id FROM ProductEntity p LEFT JOIN p.comments c GROUP BY p.id HAVING COALESCE(AVG(c.rating),0) >= ?1")
    Set<Long> findByMinimumRating(Double minimumRating);
    @Query("SELECT p FROM ProductEntity p JOIN p.observers o WHERE o = :username")
    Set<ProductEntity> findAllByObserver(@Param("username") String username);
    @Query("SELECT p.id FROM ProductEntity p JOIN p.observers o GROUP BY p.id ORDER BY COALESCE(COUNT(o), 0) DESC")
    List<Long> findTopIdsByObserversCount(Pageable pageRequest);
}