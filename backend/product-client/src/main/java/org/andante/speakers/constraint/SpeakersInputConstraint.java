package org.andante.speakers.constraint;

import org.andante.speakers.constraint.validator.SpeakersInputValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SpeakersInputValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpeakersInputConstraint {

    String message() default "{message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
