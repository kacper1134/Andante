package org.andante.forum.repository;

import org.andante.forum.repository.entity.PostResponsesLikesRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostResponsesLikesRelationRepository extends JpaRepository<PostResponsesLikesRelationEntity, Long> {

    @Query("select p from PostResponsesLikesRelationEntity p where p.response.id = ?1")
    List<PostResponsesLikesRelationEntity> findByResponseId(Long id);

    @Query("select (count(p) > 0) from PostResponsesLikesRelationEntity p " +
            "where p.response.id = ?1 and p.user.emailAddress = ?2")
    boolean isLikedByUser(Long id, String emailAddress);

    @Query("select p from PostResponsesLikesRelationEntity p where p.user.emailAddress = ?1 and p.response.id = ?2")
    PostResponsesLikesRelationEntity findByUserAndResponse(String emailAddress, Long id);
}
