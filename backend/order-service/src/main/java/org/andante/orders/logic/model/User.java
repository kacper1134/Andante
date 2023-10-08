package org.andante.orders.logic.model;

import lombok.Builder;
import lombok.Data;
import org.andante.orders.dto.ClientDTO;
import org.andante.orders.repository.entity.ClientEntity;

import java.util.Set;

@Builder
@Data
public class User {

    private Long id;
    private String emailAddress;
    private String name;
    private String phoneNumber;
    private String surname;
    private Set<Long> orderIds;

    public ClientDTO toDTO() {
        return ClientDTO.builder()
                .id(id)
                .emailAddress(emailAddress)
                .name(name)
                .phoneNumber(phoneNumber)
                .surname(surname)
                .orderIds(orderIds)
                .build();
    }

    public ClientEntity toEntity() {
        return ClientEntity.builder()
                .id(id)
                .emailAddress(emailAddress)
                .name(name)
                .phoneNumber(phoneNumber)
                .surname(surname)
                .build();
    }
}
