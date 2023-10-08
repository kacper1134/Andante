package org.andante.activity.controller.mapper;

import org.andante.activity.dto.NewsletterDTO;
import org.andante.activity.logic.model.Newsletter;
import org.springframework.stereotype.Component;

@Component
public class NewsletterDTOModelMapper {

    public NewsletterDTO toDTO(Newsletter newsletter) {
        return NewsletterDTO.builder()
                .emailAddress(newsletter.getEmailAddress())
                .subscriptionDate(newsletter.getSubscriptionDate())
                .isConfirmed(newsletter.getIsConfirmed())
                .build();
    }

    public Newsletter toModel(NewsletterDTO newsletter) {
        return Newsletter.builder()
                .emailAddress(newsletter.getEmailAddress())
                .subscriptionDate(newsletter.getSubscriptionDate())
                .isConfirmed(newsletter.getIsConfirmed())
                .build();
    }
}
