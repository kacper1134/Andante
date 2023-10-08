package org.andante.headphones.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.headphones.enums.ConstructionType;
import org.andante.headphones.enums.DriverType;
import org.andante.product.logic.model.ProductInput;

import java.util.Set;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class HeadphonesInput extends ProductInput {

    private ConstructionType constructionType;
    private DriverType driverType;
    private Boolean wireless;
    private Float bluetoothStandard;
    private Set<Long> variantsIds;
}
