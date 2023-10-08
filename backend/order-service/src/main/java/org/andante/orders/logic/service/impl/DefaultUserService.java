package org.andante.orders.logic.service.impl;

import lombok.RequiredArgsConstructor;
import org.andante.orders.exception.ClientConflictException;
import org.andante.orders.exception.ClientNotFoundException;
import org.andante.orders.logic.mapper.UserModelEntityMapper;
import org.andante.orders.logic.model.User;
import org.andante.orders.logic.service.UserService;
import org.andante.orders.repository.ClientRepository;
import org.andante.orders.repository.entity.ClientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DefaultUserService implements UserService {

    private static final String USER_CONFLICT_EXCEPTION_MESSAGE = "User with identifier %s already exists";
    private static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User with identifier %s does not exist";

    private final ClientRepository clientRepository;
    private final UserModelEntityMapper userModelEntityMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Optional<User> getById(Long id) {
        Optional<ClientEntity> databaseResponse = clientRepository.findById(id);

        return databaseResponse.map(userModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public User create(User user) {
        if (user.getId() != null && clientRepository.existsById(user.getId())) {
            throw new ClientConflictException(String.format(USER_CONFLICT_EXCEPTION_MESSAGE, user.getEmailAddress()));
        }

        ClientEntity userToCreate = userModelEntityMapper.toEntity(user);
        ClientEntity userCreated = clientRepository.save(userToCreate);

        return userModelEntityMapper.toModel(userCreated);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public User modify(User user) {
        if (user.getEmailAddress() == null || !clientRepository.existsById(user.getId())) {
            throw new ClientNotFoundException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, user.getEmailAddress()));
        }

        ClientEntity userToUpdate = userModelEntityMapper.toEntity(user);
        ClientEntity userUpdated = clientRepository.save(userToUpdate);

        return userModelEntityMapper.toModel(userUpdated);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Optional<User> delete(Long id) {
        Optional<ClientEntity> databaseResponse = clientRepository.findById(id);
        if (databaseResponse.isEmpty()) {
            throw new ClientNotFoundException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, id));
        }

        databaseResponse.ifPresent(clientRepository::delete);
        return databaseResponse.map(userModelEntityMapper::toModel);
    }
}
