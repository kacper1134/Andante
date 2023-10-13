package org.andante.headphones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.headphones.enums.ConstructionType;
import org.andante.headphones.enums.DriverType;
import org.andante.product.dto.ProductOutputDTO;

import java.util.List;
import java.util.Optional;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HeadphonesOutputDTO extends ProductOutputDTO {

    private ConstructionType constructionType;
    private DriverType driverType;
    private Boolean wireless;
    private Float bluetoothStandard;
    private List<HeadphonesVariantOutputDTO> variants;

    public Optional<Float> getBluetoothStandard() {
        return Optional.ofNullable(bluetoothStandard);
    }
}
