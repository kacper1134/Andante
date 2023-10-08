package org.andante.orders.repository.entity;

import lombok.*;
import org.andante.orders.enums.DeliveryMethod;
import org.andante.orders.enums.OrderStatus;
import org.andante.orders.enums.PaymentMethod;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Orders")
public class OrderEntity {

    @Id
    @GeneratedValue
    private Long id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime creationTimestamp;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

    @ManyToOne
    @JoinColumn(name = "delivery_id", nullable = false)
    private LocationEntity deliveryLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<OrderEntryEntity> orderEntries;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryMethod deliveryMethod;

    @Column(nullable = false)
    private Float deliveryCost;

    @Column(nullable = false)
    private Float paymentCost;

    @PreRemove
    private void removeAllEntries() {
        this.getOrderEntries()
                .forEach(entry -> entry.setOrder(null));
        Set<OrderEntity> orderUsers = client.getOrders();
        Set<OrderEntity> orderLocations = location.getOrders();
        Set<OrderEntity> orderDeliveryLocations = deliveryLocation.getOrders();
        if (orderUsers != null) {
            orderUsers.remove(this);
        }
        if(orderLocations != null) {
            orderLocations.remove(this);
        }
        if(orderDeliveryLocations != null) {
            orderDeliveryLocations.remove(this);
        }
    }
}
