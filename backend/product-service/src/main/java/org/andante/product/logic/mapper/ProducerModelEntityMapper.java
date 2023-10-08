package org.andante.product.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.product.logic.model.Producer;
import org.andante.product.repository.ProductRepository;
import org.andante.product.repository.entity.ProducerEntity;
import org.andante.product.repository.entity.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProducerModelEntityMapper {

    private final ProductRepository productRepository;

    public Producer toModel(ProducerEntity producerEntity) {
        return producerEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ProducerEntity toEntity(Producer producer) {
        Set<ProductEntity> products = new HashSet<>(productRepository.findAllById(producer.getProductsIds()));

        return ProducerEntity.builder()
                .name(producer.getName())
                .imageUrl(producer.getImageUrl())
                .websiteUrl(producer.getWebsiteUrl())
                .products(products)
                .build();
    }
}
