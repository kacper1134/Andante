package org.andante.activity.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.activity.logic.model.Newsletter;
import org.andante.activity.repository.entity.NewsletterEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class NewsletterModelEntityMapper {

    public Newsletter toModel(NewsletterEntity newsletter) {
        return Newsletter.builder()
                .emailAddress(newsletter.getEmailAddress())
                .subscriptionDate(newsletter.getSubscriptionDate())
                .isConfirmed(newsletter.getIsConfirmed())
                .build();
    }

    public NewsletterEntity toEntity(Newsletter newsletter) {
        return NewsletterEntity.builder()
                .emailAddress(newsletter.getEmailAddress())
                .subscriptionDate(newsletter.getSubscriptionDate())
                .isConfirmed(newsletter.getIsConfirmed())
                .build();
    }
}
