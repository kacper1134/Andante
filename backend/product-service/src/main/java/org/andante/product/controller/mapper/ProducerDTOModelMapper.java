package org.andante.product.controller.mapper;

import org.andante.product.dto.ProducerDTO;
import org.andante.product.logic.model.Producer;
import org.springframework.stereotype.Component;

@Component
public class ProducerDTOModelMapper {

    public ProducerDTO toDTO(Producer producer) {
        return producer.toDTO();
    }

    public Producer toModel(ProducerDTO producerDTO) {
        return Producer.builder()
                .name(producerDTO.getName())
                .websiteUrl(producerDTO.getWebsiteUrl())
                .imageUrl(producerDTO.getImageUrl())
                .productsIds(producerDTO.getProductsIds())
                .build();
    }
}
