package org.andante.orders.repository.entity;

import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "OrderEntries")
public class OrderEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(nullable = false, updatable = false)
    private Long productVariantId;

    @Column(nullable = false)
    private Integer quantity;

    @PreRemove
    private void removeEntryFromOrder() {
        if (order != null) {
            order.getOrderEntries().remove(this);
        }
    }
}
