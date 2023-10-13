package org.andante.speakers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.product.dto.ProductOutputDTO;

import java.util.List;
import java.util.Optional;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SpeakersOutputDTO extends ProductOutputDTO {

    private Boolean wireless;
    private Float bluetoothStandard;
    private List<SpeakersVariantOutputDTO> variants;

    public Optional<Float> getBluetoothStandard() {
        return Optional.ofNullable(bluetoothStandard);
    }
}
