package org.andante.forum.controller;

import dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.forum.controller.mapper.UserDTOModelMapper;
import org.andante.forum.logic.model.UserModel;
import org.andante.forum.logic.service.impl.UserServiceImpl;
import org.andante.mappers.OperationHttpStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping(value = "/forum/user", produces = MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Validated
public class UserController {

    private static final String USER_EMAIL_NULL_EMPTY_ERROR_MESSAGE = "Provided email address must not be empty nor null";

    private final UserServiceImpl userService;
    private final UserDTOModelMapper userDTOModelMapper;
    private final OperationHttpStatusMapper operationHttpStatusMapper;

    @GetMapping
    public ResponseEntity<UserDTO> get(
            @NotEmpty(message = USER_EMAIL_NULL_EMPTY_ERROR_MESSAGE)
            @RequestParam("email")
            String email
    ) {
        UserModel userModel = userService.get(email);
        UserDTO userDTO = userDTOModelMapper.toDTO(userModel);
        return ResponseEntity
                .ok()
                .body(userDTO);
    }

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody UserDTO userDTO) {
        UserModel userModel = userDTOModelMapper.toModel(userDTO);
        String serviceResponse = userService.create(userModel);
        return ResponseEntity
                .ok()
                .body(serviceResponse);
    }

    @DeleteMapping
    public ResponseEntity<OperationStatus> delete(
            @NotEmpty(message = USER_EMAIL_NULL_EMPTY_ERROR_MESSAGE)
            @RequestParam("email")
            String email
    ) {
        OperationStatus serviceResponse = userService.delete(email);
        return ResponseEntity
                .status(operationHttpStatusMapper.toHttpStatus(serviceResponse))
                .build();
    }
}
