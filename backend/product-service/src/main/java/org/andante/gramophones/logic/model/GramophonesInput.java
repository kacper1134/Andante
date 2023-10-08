package org.andante.gramophones.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.gramophones.enums.ConnectivityTechnology;
import org.andante.gramophones.enums.MotorType;
import org.andante.gramophones.enums.PowerSource;
import org.andante.gramophones.enums.TurntableMaterial;
import org.andante.product.logic.model.ProductInput;

import java.util.Set;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class GramophonesInput extends ProductInput {

    private ConnectivityTechnology connectivityTechnology;
    private TurntableMaterial turntableMaterial;
    private MotorType motorType;
    private PowerSource powerSource;
    private Integer maximumRotationalSpeed;
    private Set<Long> variantsIds;
}
