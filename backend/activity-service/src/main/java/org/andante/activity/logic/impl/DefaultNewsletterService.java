package org.andante.activity.logic.impl;

import lombok.RequiredArgsConstructor;
import org.andante.activity.exception.NewsletterConflictException;
import org.andante.activity.exception.NewsletterNotFoundException;
import org.andante.activity.logic.NewsletterService;
import org.andante.activity.logic.mapper.NewsletterModelEntityMapper;
import org.andante.activity.logic.model.Newsletter;
import org.andante.activity.repository.NewsletterRepository;
import org.andante.activity.repository.entity.NewsletterEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DefaultNewsletterService implements NewsletterService {

    private static final String NEWSLETTER_CONFLICT_EXCEPTION_MESSAGE = "%s have already been subscribed to the newsletter";
    private static final String NEWSLETTER_NOT_FOUND_EXCEPTION_MESSAGE = "%s have not been subscribed to the newsletter";

    private final NewsletterRepository newsletterRepository;
    private final NewsletterModelEntityMapper newsletterMapper;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Newsletter subscribe(String emailAddress) {
        if (newsletterRepository.existsById(emailAddress)) {
            throw new NewsletterConflictException(String.format(NEWSLETTER_CONFLICT_EXCEPTION_MESSAGE, emailAddress));
        }

        NewsletterEntity subscriptionToPersist = createNewSubscription(emailAddress);

        NewsletterEntity persistedSubscription = newsletterRepository.save(subscriptionToPersist);

        return newsletterMapper.toModel(persistedSubscription);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Newsletter confirmSubscription(String emailAddress) {
        Optional<NewsletterEntity> entityToUpdate = newsletterRepository.findById(emailAddress);

        if (entityToUpdate.isEmpty()) {
            throw new NewsletterNotFoundException(String.format(NEWSLETTER_NOT_FOUND_EXCEPTION_MESSAGE, emailAddress));
        }

        NewsletterEntity newsletterEntity = entityToUpdate.get();

        newsletterEntity.setIsConfirmed(true);

        NewsletterEntity persistedNewsletterEntity = newsletterRepository.save(newsletterEntity);

        return newsletterMapper.toModel(persistedNewsletterEntity);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Newsletter unsubscribe(String emailAddress) {
        Optional<NewsletterEntity> subscriptionToDelete = newsletterRepository.findById(emailAddress);

        if (subscriptionToDelete.isEmpty()) {
            throw new NewsletterNotFoundException(String.format(NEWSLETTER_NOT_FOUND_EXCEPTION_MESSAGE, emailAddress));
        }

        NewsletterEntity deletedSubscription = subscriptionToDelete.get();

        newsletterRepository.delete(deletedSubscription);

        return newsletterMapper.toModel(deletedSubscription);
    }

    private NewsletterEntity createNewSubscription(String emailAddress) {
        return NewsletterEntity.builder()
                .emailAddress(emailAddress)
                .isConfirmed(false)
                .build();
    }
}
