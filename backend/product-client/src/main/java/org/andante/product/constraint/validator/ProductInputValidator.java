package org.andante.product.constraint.validator;

import org.andante.product.constraint.ProductInputConstraint;
import org.andante.product.dto.ProductInputDTO;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProductInputValidator implements ConstraintValidator<ProductInputConstraint, ProductInputDTO> {

    @Override
    public boolean isValid(ProductInputDTO validatedProduct, ConstraintValidatorContext constraintValidatorContext) {
        return areFrequenciesValid(validatedProduct, constraintValidatorContext);
    }

    private boolean areFrequenciesValid(ProductInputDTO validatedProduct, ConstraintValidatorContext constraintValidatorContext) {
        Long minimumFrequency = validatedProduct.getMinimumFrequency();
        Long maximumFrequency = validatedProduct.getMaximumFrequency();

        if (minimumFrequency != null && maximumFrequency != null) {
            constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class).addMessageParameter("message",
                    String.format("Maximum frequency '%d' must not be lesser than minimum frequency '%d'", maximumFrequency, minimumFrequency));

            return maximumFrequency >= minimumFrequency;
        }

        return true;
    }
}
