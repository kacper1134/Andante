package org.andante.product.repository;

import org.andante.product.repository.entity.ProducerEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProducerRepository extends JpaRepository<ProducerEntity, String> {

    @Query("SELECT pr FROM ProducerEntity pr LEFT JOIN pr.products p GROUP BY pr ORDER BY COALESCE(COUNT(p), 0) DESC")
    List<ProducerEntity> getBiggestProducers(Pageable pageable);
}
