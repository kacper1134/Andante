package org.andante.microphones.constraint.validator;

import org.andante.microphones.constraint.MicrophonesInputConstraint;
import org.andante.microphones.dto.MicrophonesInputDTO;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class MicrophonesInputValidator implements ConstraintValidator<MicrophonesInputConstraint, MicrophonesInputDTO> {

    @Override
    public boolean isValid(MicrophonesInputDTO validatedMicrophones, ConstraintValidatorContext context) {
        return isBluetoothStandardCorrectlyProvided(validatedMicrophones, context);
    }

    private boolean isBluetoothStandardCorrectlyProvided(MicrophonesInputDTO validatedMicrophones, ConstraintValidatorContext context) {
        Boolean areWireless = validatedMicrophones.getWireless();
        Optional<Float> standard = validatedMicrophones.getBluetoothStandard();

        if (!areWireless && standard.isPresent()) {
            setConstraintViolationMessage(context,
                    "Microphones marked as not wireless should not specify a bluetooth standard");

            return false;
        }

        if (areWireless && standard.isEmpty()) {
                    setConstraintViolationMessage(context,
                            "Microphones marked as wireless should specify a bluetooth standard");

            return false;
        }

        return true;
    }

    private void setConstraintViolationMessage(ConstraintValidatorContext context, String message) {
        context.unwrap(HibernateConstraintValidatorContext.class).addMessageParameter("message",
                message);
    }
}
