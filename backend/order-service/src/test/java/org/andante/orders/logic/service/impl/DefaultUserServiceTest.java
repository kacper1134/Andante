package org.andante.orders.logic.service.impl;

import org.andante.orders.exception.ClientConflictException;
import org.andante.orders.exception.ClientNotFoundException;
import org.andante.orders.logic.mapper.OrderModelEntityMapper;
import org.andante.orders.logic.mapper.UserModelEntityMapper;
import org.andante.orders.logic.model.User;
import org.andante.orders.repository.ClientRepository;
import org.andante.orders.repository.entity.ClientEntity;
import org.andante.orders.utils.OrderTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(TestContainersExtension.class)
@Import(OrderTestUtils.class)
@Transactional
public class DefaultUserServiceTest {

    @Autowired
    private OrderTestUtils orderTestUtils;

    @Autowired
    private DefaultUserService service;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderModelEntityMapper orderModelEntityMapper;

    @Autowired
    private UserModelEntityMapper mapper;

    @Nested
    @DisplayName("Get By Id")
    class GetByIdTests {

        @Test
        @DisplayName("should return empty set for empty database")
        void shouldReturnNothingForEmptyDatabase() {
            // given
            Long id = 1L;

            // when
            Optional<User> serviceResponse = service.getById(id);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return existing user")
        void shouldReturnExistingUser() {
            // given
            ClientEntity clientToPersist = prepareClient();

            ClientEntity persistedClient = clientRepository.save(clientToPersist);

            Long id = persistedClient.getId();

            // when
            Optional<User> serviceResponse = service.getById(id);

            // then
            assertThat(serviceResponse).isNotNull();
        }
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {

        @Test
        @DisplayName("should throw ClientConflictException if client with given identifier exists")
        void shouldThrowClientConflictExceptionIfClientWithGivenIdentifierExists() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());

            User userToCreate = mapper.toModel(client);
            // when
            // always

            // then
            assertThatThrownBy(() -> service.create(userToCreate))
                    .isInstanceOf(ClientConflictException.class);
        }

        @Test
        @DisplayName("should create user if model is valid")
        void shouldCreateUserIfModelIsValid() {
            // given
            User userToCreate = orderTestUtils.generate(User.class);

            // when
            User serviceResponse = service.create(userToCreate);

            // then
            Optional<User> persistedUser = clientRepository.findById(serviceResponse.getId()).map(mapper::toModel);

            assertThat(persistedUser).isPresent();
            assertThat(persistedUser.get()).isEqualTo(serviceResponse);
        }
    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @DisplayName("should throw UserNotFoundException if user with given identifier does not exist")
        void shouldThrowClientConflictExceptionIfClientWithGivenIdentifierExists() {
            // given
            User userToCreate = orderTestUtils.generate(User.class);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.modify(userToCreate))
                    .isInstanceOf(ClientNotFoundException.class);
        }

        @Test
        @DisplayName("should update user if it exists")
        void shouldUpdateUserIfItExists() {
            // given
            ClientEntity persistedClient = clientRepository.save(prepareClient());

            User expectedResult = mapper.toModel(persistedClient);

            // when
            User serviceResponse = service.modify(expectedResult);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @DisplayName("should throw ClientNotFoundException if client does not exist")
        void shouldThrowClientNotFoundExceptionIfClientDoesNotExist() {
            // given
            Long id = 1L;

            // when
            // always

            // then
            assertThatThrownBy(() -> service.delete(id))
                    .isInstanceOf(ClientNotFoundException.class)
                    .hasMessageContaining(String.valueOf(id));
        }

        @Test
        @DisplayName("should delete existing client")
        void shouldDeleteExistingClient() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());

            User expectedResult = mapper.toModel(client);

            // when
            Optional<User> serviceResponse = service.delete(client.getId());

            // then
            assertThat(clientRepository.findById(client.getId())).isEmpty();
            assertThat(serviceResponse).isNotNull().isEqualTo(Optional.of(expectedResult));
        }
    }

    private ClientEntity prepareClient(){
        ClientEntity client = orderTestUtils.generate(ClientEntity.class);
        client.setEmailAddress("user@gmail.com");
        client.setPhoneNumber("123456789");
        client.setOrders(Set.of());
        return client;
    }
}
