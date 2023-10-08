package org.andante.product.logic.model;

import lombok.Builder;
import lombok.Data;
import org.andante.product.dto.ProducerDTO;
import org.andante.product.repository.entity.ProducerEntity;

import java.util.Set;

@Builder
@Data
public class Producer {

    private String name;
    private String websiteUrl;
    private String imageUrl;
    private Set<Long> productsIds;

    public ProducerDTO toDTO() {
        return ProducerDTO.builder()
                .name(name)
                .imageUrl(imageUrl)
                .websiteUrl(websiteUrl)
                .productsIds(productsIds)
                .build();
    }

    public ProducerEntity toEntity() {
        return ProducerEntity.builder()
                .name(name)
                .imageUrl(imageUrl)
                .websiteUrl(websiteUrl)
                .build();
    }
}
