package org.andante.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDTO {

    @NotBlank(message = "Activity identifier '${validatedValue}' must not be blank")
    private String id;

    @NotNull(message = "Activity priority must be one of predefined values")
    private Priority priority;

    @NotNull(message = "Activity domain must be one of predefined values")
    private Domain domain;

    @NotBlank(message = "Activity related identifier '${validatedValue}' must not be blank")
    private String relatedId;

    @NotNull(message = "Activity affected users '${validatedValue}' must not be a null value")
    private Set<@NotBlank(message = "Activity affected user name '${validatedValue}' must not be blank") String> affectedUsers;

    @NotNull(message = "Activity acknowledged users '${validatedValue}' must not be a null value")
    private Set<@NotBlank(message = "Activity acknowledged user email '${validatedValue}' must not be blank") String> acknowledgedUsers;

    @Size(max = 1000, message = "Activity description '${validatedValue}' must be between {min} and {max} characters long")
    @NotNull(message = "Activity description must not be a null")
    private String description;

    @NotNull(message = "Activity event timestamp '${validatedValue}' must be of correct formatting")
    private LocalDateTime eventTimestamp;
}
