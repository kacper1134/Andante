package org.andante.product.repository.entity;

import lombok.*;
import org.andante.product.logic.model.Producer;

import javax.persistence.*;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Producents")
public class ProducerEntity {

    @Id
    @Column(length = 100)
    private String name;

    @Column(nullable = false, name="website_url", length = 200)
    private String websiteUrl;

    @Column(nullable = false, name="image_url", length = 200)
    private String imageUrl;

    @OneToMany(mappedBy = "producer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<ProductEntity> products;

    public Producer toModel() {
        return Producer.builder()
                .name(name)
                .websiteUrl(websiteUrl)
                .imageUrl(imageUrl)
                .productsIds(Optional.ofNullable(products).orElse(Set.of()).stream()
                        .map(ProductEntity::getId)
                        .collect(Collectors.toSet()))
                .build();
    }
}
