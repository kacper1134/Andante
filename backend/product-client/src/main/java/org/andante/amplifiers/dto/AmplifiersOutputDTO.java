package org.andante.amplifiers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.amplifiers.enums.AmplifierType;
import org.andante.product.dto.ProductOutputDTO;

import java.util.List;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AmplifiersOutputDTO extends ProductOutputDTO {

    private Float power;
    private AmplifierType amplifierType;
    private List<AmplifiersVariantOutputDTO> variants;
}
