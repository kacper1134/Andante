package org.andante.activity.repository;

import org.andante.activity.repository.entity.ActivityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, String>, JpaSpecificationExecutor<ActivityEntity> {
    Page<ActivityEntity> findByAffectsAllTrue(Pageable pageable);
    @Query("SELECT a FROM ActivityEntity a LEFT JOIN a.affectedUsers u WHERE u = :user OR a.affectsAll = true GROUP BY a")
    Page<ActivityEntity> findAllByAffectedUsersContainingOrAffectsAllTrue(@Param("user") String user, Pageable pageable);
    @Query("SELECT a FROM ActivityEntity a JOIN a.acknowledgedUsers u WHERE u = :user")
    Page<ActivityEntity> findAllByAcknowledgedUsersContaining(@Param("user") String user, Pageable pageable);
    @Query("SELECT a FROM ActivityEntity a LEFT JOIN a.affectedUsers u WHERE (u = :user OR a.affectsAll = true) AND a NOT IN (SELECT i FROM ActivityEntity i JOIN i.acknowledgedUsers u WHERE u = :user)")
    Page<ActivityEntity> findAllByAcknowledgedUsersNotContaining(@Param("user") String user, Pageable pageable);
}
