package org.andante.headphones.dto;

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
public class HeadphonesVariantOutputDTO extends ProductVariantOutputDTO {

    private Integer nominalImpedance;
    private Integer loudness;
    private String color;
    private Long headphonesId;
}
