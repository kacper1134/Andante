package org.andante.orders.repository;

import org.andante.orders.repository.entity.OrderEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface OrderEntryRepository extends JpaRepository<OrderEntryEntity, Long> {
    Set<OrderEntryEntity> findAllByOrderId(Long orderId);
}
