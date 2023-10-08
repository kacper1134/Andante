package org.andante.amplifiers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.product.dto.ProductVariantOutputDTO;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AmplifiersVariantOutputDTO extends ProductVariantOutputDTO {

    private String color;
    private Long amplifiersId;
}
