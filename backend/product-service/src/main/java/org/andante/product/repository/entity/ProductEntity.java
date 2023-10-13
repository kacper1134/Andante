package org.andante.product.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.andante.product.enums.ProductType;
import org.andante.product.logic.model.ProductOutput;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Products")
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 5000)
    private String description;

    @Column(nullable = false)
    private Float weight;

    @Column(nullable = false, name="price")
    private BigDecimal basePrice;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    @CreatedDate
    @Column(name = "created", updatable = false)
    private LocalDateTime creationTimestamp;

    @LastModifiedDate
    @Column(name = "last_modified")
    private LocalDateTime modificationTimestamp;

    @OneToMany(mappedBy="product", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<CommentEntity> comments;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="Users", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "observer")
    private Set<String> observers;

    @Column(nullable = false, name = "min_freq")
    private Long minimumFrequency;

    @Column(nullable = false, name = "max_freq")
    private Long maximumFrequency;

    @ManyToOne
    @JoinColumn(name = "producer_name", nullable = false)
    private ProducerEntity producer;

    @Column(name = "producer_name", nullable = false, insertable = false, updatable = false)
    private String producerName;

    public abstract List<ProductVariantEntity> getVariants();
    public abstract void setVariants(Set<ProductVariantEntity> variants);

    public abstract ProductOutput toModel();

    @PrePersist
    public void prePersist() {
        if (producer != null) {
            Set<ProductEntity> producerProducts = Optional.ofNullable(producer.getProducts())
                    .orElse(new HashSet<>());

            producerProducts.add(this);

            producer.setProducts(producerProducts);
        }
    }

    @PreRemove
    public void preRemove() {
        if (producer != null) {
            Set<ProductEntity> producerProducts = Optional.ofNullable(producer.getProducts())
                    .orElse(new HashSet<>());

            producerProducts.remove(this);

            producer.setProducts(producerProducts);
        }
    }
}
