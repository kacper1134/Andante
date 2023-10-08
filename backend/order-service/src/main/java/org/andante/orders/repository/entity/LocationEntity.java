package org.andante.orders.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Location")
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String street;

    @Column(nullable = false, length = 50)
    private String streetNumber;

    @Column
    private Long flatNumber;

    @Column(nullable = false, name = "post_code", length = 6)
    private String postCode;

    @OneToMany(mappedBy = "location")
    private Set<OrderEntity> orders;

    @OneToMany(mappedBy = "deliveryLocation")
    private Set<OrderEntity> deliveryOrders;
}
