package org.andante.orders.repository;

import org.andante.orders.enums.OrderStatus;
import org.andante.orders.repository.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    @Query("SELECT o FROM OrderEntity o JOIN o.client c ON c.emailAddress = :email WHERE o.orderStatus = :status GROUP BY o")
    Page<OrderEntity> findAllByClientEmailAndStatus(@Param("email") String email, @Param("status") OrderStatus status, Pageable pageable);
}
