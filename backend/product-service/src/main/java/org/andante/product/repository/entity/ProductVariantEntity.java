package org.andante.product.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.andante.product.logic.model.ProductVariantOutput;

import javax.persistence.*;
import java.math.BigDecimal;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ProductVariants")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ProductVariantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, name = "price_diff")
    private BigDecimal priceDifference;

    @Column(nullable = false, name="quantity")
    private Integer availableQuantity;

    @Column(nullable = false, name="thumbnail_url", length = 200)
    private String thumbnailUrl;

    @Column(nullable = false, name="image_url", length = 200)
    private String imageUrl;

    public abstract ProductEntity getProduct();
    public abstract void setProduct(ProductEntity product);
    public abstract ProductVariantOutput toModel();
}
