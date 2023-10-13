package org.andante.gramophones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.gramophones.enums.ConnectivityTechnology;
import org.andante.gramophones.enums.MotorType;
import org.andante.gramophones.enums.PowerSource;
import org.andante.gramophones.enums.TurntableMaterial;
import org.andante.product.dto.ProductOutputDTO;

import java.util.List;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GramophonesOutputDTO extends ProductOutputDTO {

    private ConnectivityTechnology connectivityTechnology;
    private TurntableMaterial turntableMaterial;
    private MotorType motorType;
    private PowerSource powerSource;
    private Integer maximumRotationalSpeed;
    private List<GramophonesVariantOutputDTO> variants;
}
