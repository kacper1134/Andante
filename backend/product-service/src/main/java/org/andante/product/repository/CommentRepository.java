package org.andante.product.repository;

import org.andante.product.repository.entity.CommentEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long>, JpaSpecificationExecutor<CommentEntity> {
    Set<CommentEntity> findAllByProductId(Long productId);
    Integer countByUsername(String username);
    @Query("SELECT COUNT(o) FROM CommentEntity c JOIN c.observers o WHERE c.username = :username")
    Integer countLikes(@Param("username") String username);
    @Query("SELECT c FROM CommentEntity c LEFT JOIN c.observers o WHERE c.username = :username GROUP BY c ORDER BY COALESCE(COUNT(o), 0) DESC")
    List<CommentEntity> findTopPosts(@Param("username") String username, PageRequest pageRequest);
    @Query("SELECT c FROM CommentEntity c JOIN c.observers o WHERE o = :email")
    Set<CommentEntity> findAllByObserver(@Param("email") String email);
}
