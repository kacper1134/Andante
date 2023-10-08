package org.andante.orders.logic.service;

import org.andante.orders.logic.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getById(Long id);
    User create(User user);
    User modify(User user);
    Optional<User> delete(Long id);
}
