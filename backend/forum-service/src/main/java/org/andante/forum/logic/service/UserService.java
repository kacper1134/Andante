package org.andante.forum.logic.service;

import org.andante.enums.OperationStatus;
import org.andante.forum.logic.model.UserModel;

public interface UserService {

    UserModel get(String email);
    String create(UserModel userModel);
    OperationStatus delete(String email);
}
