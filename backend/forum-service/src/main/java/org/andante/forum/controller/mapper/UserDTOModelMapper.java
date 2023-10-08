package org.andante.forum.controller.mapper;

import dto.UserDTO;
import org.andante.forum.logic.model.UserModel;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class UserDTOModelMapper {

    public UserModel toModel(UserDTO userDTO) {
        return UserModel.builder()
                .emailAddress(userDTO.getEmail())
                .surname(userDTO.getSurname())
                .username(userDTO.getUsername())
                .name(userDTO.getName())
                .posts(Collections.emptySet())
                .responses(Collections.emptySet())
                .likedPosts(Collections.emptySet())
                .likedResponses(Collections.emptySet())
                .build();
    }

    public UserDTO toDTO(UserModel userModel) {
        return UserDTO.builder()
                .email(userModel.getEmailAddress())
                .surname(userModel.getSurname())
                .username(userModel.getUsername())
                .name(userModel.getName())
                .build();
    }
}
