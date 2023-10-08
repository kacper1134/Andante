package org.andante.forum.repository;

import org.andante.forum.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    @Query("select u from UserEntity u where u.emailAddress = ?1")
    Optional<UserEntity> findByEmail(String emailAddress);
}
