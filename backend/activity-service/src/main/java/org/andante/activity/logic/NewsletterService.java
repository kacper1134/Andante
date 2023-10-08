package org.andante.activity.logic;

import org.andante.activity.logic.model.Newsletter;

public interface NewsletterService {
    Newsletter subscribe(String emailAddress);
    Newsletter confirmSubscription(String emailAddress);
    Newsletter unsubscribe(String emailAddress);
}
