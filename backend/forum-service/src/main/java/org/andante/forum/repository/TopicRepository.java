package org.andante.forum.repository;

import org.andante.forum.repository.entity.TopicEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<TopicEntity, Long>, JpaSpecificationExecutor<TopicEntity> {
    @Query("select t from TopicEntity t where t.parentTopic.id = ?1")
    List<TopicEntity> findByParentTopicId(Long id);

    @Query("SELECT t FROM TopicEntity t LEFT JOIN t.posts p WHERE t.parentTopic IS NULL GROUP BY t ORDER BY COALESCE(COUNT(p), 0) DESC")
    Page<TopicEntity> findMostPopular(Pageable pageable);
}
