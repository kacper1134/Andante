package org.andante.speakers.dto;

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
public class SpeakersVariantOutputDTO extends ProductVariantOutputDTO {

    private Integer loudness;
    private String color;
    private Long speakersId;
}
