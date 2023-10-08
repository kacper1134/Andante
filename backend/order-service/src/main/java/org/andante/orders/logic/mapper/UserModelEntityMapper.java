package org.andante.orders.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.orders.logic.model.User;
import org.andante.orders.repository.OrderRepository;
import org.andante.orders.repository.entity.ClientEntity;
import org.andante.orders.repository.entity.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserModelEntityMapper {
    private final OrderRepository orderRepository;

    public User toModel(ClientEntity clientEntity) {
        return clientEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ClientEntity toEntity(User user) {
        Set<OrderEntity> orders = new HashSet<>(orderRepository.findAllById(user.getOrderIds()));

        return ClientEntity.builder()
                .id(user.getId())
                .emailAddress(user.getEmailAddress())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .surname(user.getSurname())
                .orders(orders)
                .build();
    }
}
