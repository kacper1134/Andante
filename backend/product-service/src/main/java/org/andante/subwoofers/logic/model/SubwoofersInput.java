package org.andante.subwoofers.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.product.logic.model.ProductInput;
import org.andante.subwoofers.enums.SubwooferType;

import java.util.Set;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class SubwoofersInput extends ProductInput {

    private Float power;
    private SubwooferType type;
    private Set<Long> variantsIds;
}
