package org.andante.forum.repository;

import org.andante.forum.repository.entity.PostResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostResponseRepository extends JpaRepository<PostResponseEntity, Long>, JpaSpecificationExecutor<PostResponseEntity> {

    @Query("select p from PostResponseEntity p where p.post.id = ?1")
    List<PostResponseEntity> findByPostId(Long id);
}
