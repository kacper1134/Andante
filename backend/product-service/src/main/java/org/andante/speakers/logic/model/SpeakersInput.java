package org.andante.speakers.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.product.logic.model.ProductInput;

import java.util.Set;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class SpeakersInput extends ProductInput {

    private Boolean wireless;
    private Float bluetoothStandard;
    private Set<Long> variantsIds;
}
