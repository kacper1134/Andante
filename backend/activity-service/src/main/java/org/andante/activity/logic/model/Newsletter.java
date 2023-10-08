package org.andante.activity.logic.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class Newsletter {

    private String emailAddress;
    private LocalDateTime subscriptionDate;
    private Boolean isConfirmed;
}
