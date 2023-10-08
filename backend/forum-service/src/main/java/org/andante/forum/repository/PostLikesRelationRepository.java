package org.andante.forum.repository;

import org.andante.forum.repository.entity.PostEntity;
import org.andante.forum.repository.entity.PostLikesRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PostLikesRelationRepository extends JpaRepository<PostLikesRelationEntity, Long>, JpaSpecificationExecutor<PostEntity> {
    @Query("select p from PostLikesRelationEntity p where p.post.id = ?1")
    List<PostLikesRelationEntity> findByPostId(Long id);
    @Query("SELECT p FROM PostLikesRelationEntity l JOIN UserEntity u ON u.emailAddress = :email JOIN l.post p")
    Set<PostEntity> findAllPostsByEmailAddress(@Param("email") String emailAddress);
    @Query("select p from PostLikesRelationEntity p where p.user.emailAddress = ?1 and p.post.id = ?2")
    PostLikesRelationEntity findByUserAndPost(String emailAddress, Long id);
    @Query("select (count(p) > 0) from PostLikesRelationEntity p where p.post.id = ?1 and p.user.emailAddress = ?2")
    boolean isLikedByUser(Long id, String emailAddress);
    @Query("select p from PostLikesRelationEntity p where p.post.id = ?1 and p.user.emailAddress = ?2")
    Optional<PostLikesRelationEntity> getByPostAndEmail(Long id, String emailAddress);
}
