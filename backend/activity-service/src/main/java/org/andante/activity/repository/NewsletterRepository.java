package org.andante.activity.repository;

import org.andante.activity.repository.entity.NewsletterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsletterRepository extends JpaRepository<NewsletterEntity, String> {
}
