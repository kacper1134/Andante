package org.andante.amplifiers.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.amplifiers.enums.AmplifierType;
import org.andante.product.logic.model.ProductInput;

import java.util.Set;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AmplifiersInput extends ProductInput {

    private Float power;
    private AmplifierType amplifierType;
    private Set<Long> variantsIds;
}
