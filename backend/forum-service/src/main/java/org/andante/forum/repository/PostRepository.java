package org.andante.forum.repository;

import org.andante.forum.repository.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>, JpaSpecificationExecutor<PostEntity> {

    @Query("select p from PostEntity p where p.topic.id = ?1")
    List<PostEntity> findAllByTopicId(Long id);

    @Query("SELECT p FROM PostEntity p LEFT JOIN p.userLikes l LEFT JOIN p.responses r " +
            "GROUP BY p ORDER BY COALESCE(COUNT(l), 0) + COALESCE(COUNT(r), 0) DESC")
    Page<PostEntity> getAllTopPosts(Pageable pageable);

    @Query("SELECT p FROM PostEntity p LEFT JOIN p.userLikes l LEFT JOIN p.responses r WHERE p.topic.id = ?1 GROUP BY p ORDER BY COALESCE(COUNT(l), 0) + COALESCE(COUNT(r), 0) DESC")
    Page<PostEntity> getTopicTopPosts(Long id, Pageable pageable);
}
