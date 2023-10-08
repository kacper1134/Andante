package org.andante.product.logic.mapper;

import org.andante.product.logic.model.ProductOutput;
import org.andante.product.repository.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductModelEntityMapper {

    public ProductOutput toModel(ProductEntity productEntity) {
        return productEntity.toModel();
    }
}
