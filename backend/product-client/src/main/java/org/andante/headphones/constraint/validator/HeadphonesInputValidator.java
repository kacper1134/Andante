package org.andante.headphones.constraint.validator;

import org.andante.headphones.constraint.HeadphonesInputConstraint;
import org.andante.headphones.dto.HeadphonesInputDTO;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class HeadphonesInputValidator implements ConstraintValidator<HeadphonesInputConstraint, HeadphonesInputDTO> {

    @Override
    public boolean isValid(HeadphonesInputDTO validatedHeadphones, ConstraintValidatorContext context) {
        return isBluetoothStandardCorrectlyProvided(validatedHeadphones, context);
    }

    private boolean isBluetoothStandardCorrectlyProvided(HeadphonesInputDTO validatedHeadphones, ConstraintValidatorContext context) {
        Boolean areWireless = validatedHeadphones.getWireless();
        Optional<Float> standard = validatedHeadphones.getBluetoothStandard();

        if (!areWireless && standard.isPresent()) {
            setConstraintViolationMessage(context,
                    "Headphones marked as not wireless should not specify a bluetooth standard");

            return false;
        }

        if (areWireless && standard.isEmpty()) {
                    setConstraintViolationMessage(context,
                            "Headphones marked as wireless should specify a bluetooth standard");

            return false;
        }

        return true;
    }

    private void setConstraintViolationMessage(ConstraintValidatorContext context, String message) {
        context.unwrap(HibernateConstraintValidatorContext.class).addMessageParameter("message",
                message);
    }
}
