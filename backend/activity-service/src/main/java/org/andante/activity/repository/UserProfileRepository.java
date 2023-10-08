package org.andante.activity.repository;

import org.andante.activity.repository.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, String>, JpaSpecificationExecutor<UserProfileEntity> {
    List<UserProfileEntity> findAllByUsernameIsIn(List<String> userNames);

    Optional<UserProfileEntity> findByUsername(String username);
}
