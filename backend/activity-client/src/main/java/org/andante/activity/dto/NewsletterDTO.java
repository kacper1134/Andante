package org.andante.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewsletterDTO {

    private String emailAddress;
    private LocalDateTime subscriptionDate;
    private Boolean isConfirmed;
}
