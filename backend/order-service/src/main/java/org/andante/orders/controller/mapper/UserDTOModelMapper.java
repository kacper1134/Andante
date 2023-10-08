package org.andante.orders.controller.mapper;

import org.andante.orders.dto.ClientDTO;
import org.andante.orders.logic.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDTOModelMapper {

    public ClientDTO toDTO(User user) {
        return user.toDTO();
    }

    public User toModel(ClientDTO clientDTO) {
        return User.builder()
                .id(clientDTO.getId())
                .emailAddress(clientDTO.getEmailAddress())
                .name(clientDTO.getName())
                .phoneNumber(clientDTO.getPhoneNumber())
                .surname(clientDTO.getSurname())
                .orderIds(clientDTO.getOrderIds())
                .build();
    }
}
