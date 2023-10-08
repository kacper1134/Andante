package org.andante.orders.repository.entity;

import lombok.*;
import org.andante.orders.logic.model.User;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Users")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "email_address")
    private String emailAddress;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String surname;

    @Column(nullable = false, name = "phone_number", length = 9)
    private String phoneNumber;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<OrderEntity> orders;

    public User toModel() {
        return User.builder()
                .id(id)
                .emailAddress(emailAddress)
                .name(name)
                .phoneNumber(phoneNumber)
                .surname(surname)
                .orderIds(orders.stream()
                        .map(OrderEntity::getId)
                        .collect(Collectors.toSet()))
                .build();
    }
}
