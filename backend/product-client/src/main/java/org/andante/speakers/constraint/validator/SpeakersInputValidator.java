package org.andante.speakers.constraint.validator;

import org.andante.speakers.constraint.SpeakersInputConstraint;
import org.andante.speakers.dto.SpeakersInputDTO;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class SpeakersInputValidator implements ConstraintValidator<SpeakersInputConstraint, SpeakersInputDTO> {

    @Override
    public boolean isValid(SpeakersInputDTO validatedSpeakers, ConstraintValidatorContext context) {
        return isBluetoothStandardCorrectlyProvided(validatedSpeakers, context);
    }

    private boolean isBluetoothStandardCorrectlyProvided(SpeakersInputDTO validatedSpeakers, ConstraintValidatorContext context) {
        Boolean areWireless = validatedSpeakers.getWireless();
        Optional<Float> standard = validatedSpeakers.getBluetoothStandard();

        if (!areWireless && standard.isPresent()) {
            setConstraintViolationMessage(context,
                    "Speakers marked as not wireless should not specify a bluetooth standard");

            return false;
        }

        if (areWireless && standard.isEmpty()) {
                    setConstraintViolationMessage(context,
                            "Speakers marked as wireless should specify a bluetooth standard");

            return false;
        }

        return true;
    }

    private void setConstraintViolationMessage(ConstraintValidatorContext context, String message) {
        context.unwrap(HibernateConstraintValidatorContext.class).addMessageParameter("message",
                message);
    }
}
