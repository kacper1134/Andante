package org.andante.orders.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.orders.controller.mapper.UserDTOModelMapper;
import org.andante.orders.dto.ClientDTO;
import org.andante.orders.logic.model.User;
import org.andante.orders.logic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Optional;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RequestMapping("/order/user")
@Validated
public class UserController {

    private static final String POSITIVE_IDENTIFIER_ERROR_MESSAGE = "Provided value '${validatedValue}' must be a positive number";
    private static final String NULL_IDENTIFIER_ERROR_MESSAGE = "Identifier must not be null";

    private final UserService userService;
    private final UserDTOModelMapper userDTOModelMapper;

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> get(@Positive(message = POSITIVE_IDENTIFIER_ERROR_MESSAGE) @NotNull(message = NULL_IDENTIFIER_ERROR_MESSAGE) @PathVariable("id") Long id) {
        Optional<User> serviceResponse = userService.getById(id);
        Optional<ClientDTO> userFound = serviceResponse.map(userDTOModelMapper::toDTO);

        if (userFound.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ResponseEntity.ok(userFound.get());
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody ClientDTO clientToCreate) {
        User model = userDTOModelMapper.toModel(clientToCreate);
        User serviceResponse = userService.create(model);

        return ResponseEntity.ok()
                .body(serviceResponse.getId());
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(@Valid @RequestBody ClientDTO clientToModify) {
        User model = userDTOModelMapper.toModel(clientToModify);
        userService.modify(model);

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@Positive(message = POSITIVE_IDENTIFIER_ERROR_MESSAGE) @NotNull(message = NULL_IDENTIFIER_ERROR_MESSAGE) @PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.ok()
                .build();
    }
}
