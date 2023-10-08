package org.andante.forum.logic.service.impl;

import exception.UserConflictException;
import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.forum.logic.mapper.UserModelEntityMapper;
import org.andante.forum.logic.model.UserModel;
import org.andante.forum.logic.service.UserService;
import org.andante.forum.repository.UserRepository;
import org.andante.forum.repository.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserServiceImpl implements UserService {

    private static final String USER_CONFLICT_EXCEPTION_MESSAGE = "User %s already exists";

    private final UserRepository userRepository;
    private final UserModelEntityMapper userModelEntityMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public UserModel get(String email) {
        Optional<UserEntity> databaseResponse = userRepository.findById(email);
        UserEntity userEntity = databaseResponse.get();
        return userModelEntityMapper.toModel(userEntity);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String create(UserModel userModel) {
        if (userModel.getEmailAddress() != null) {
            Optional<UserEntity> databaseResponse = userRepository.findById(userModel.getEmailAddress());

            if (databaseResponse.isPresent()) {
                return databaseResponse.get().getEmailAddress();
            }
        }
        UserEntity userEntity = userModelEntityMapper.toEntity(userModel);
        UserEntity savedUserEntity = userRepository.save(userEntity);
        return savedUserEntity.getEmailAddress();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OperationStatus delete(String email) {
        Optional<UserEntity> databaseResponse = userRepository.findByEmail(email);
        databaseResponse.ifPresent(userRepository::delete);
        return databaseResponse.isPresent() ? OperationStatus.OK : OperationStatus.NOT_FOUND;
    }
}
