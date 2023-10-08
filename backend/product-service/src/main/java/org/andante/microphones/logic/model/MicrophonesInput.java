package org.andante.microphones.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.microphones.enums.MicrophoneType;
import org.andante.product.logic.model.ProductInput;

import java.util.Set;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class MicrophonesInput extends ProductInput {

    private Boolean wireless;
    private Float bluetoothStandard;
    private MicrophoneType type;
    private Set<Long> variantsIds;
}
