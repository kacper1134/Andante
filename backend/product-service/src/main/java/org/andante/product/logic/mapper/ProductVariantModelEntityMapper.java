package org.andante.product.logic.mapper;

import org.andante.product.logic.model.ProductVariantInput;
import org.andante.product.logic.model.ProductVariantOutput;
import org.andante.product.repository.entity.ProductVariantEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductVariantModelEntityMapper {

    public ProductVariantOutput toModel(ProductVariantEntity productVariantEntity) {
        return productVariantEntity.toModel();
    }

    public ProductVariantEntity toEntity(ProductVariantInput productVariantInput) {
        return productVariantInput.toEntity();
    }
}
