package org.andante.microphones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.microphones.enums.MicrophoneType;
import org.andante.product.dto.ProductOutputDTO;

import java.util.List;
import java.util.Optional;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MicrophonesOutputDTO extends ProductOutputDTO {

    private Boolean wireless;
    private Float bluetoothStandard;
    private MicrophoneType type;
    private List<MicrophonesVariantOutputDTO> variants;

    public Optional<Float> getBluetoothStandard() {
        return Optional.ofNullable(bluetoothStandard);
    }
}
