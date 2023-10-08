package org.andante.product.logic.service.impl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.product.dto.ProductQuerySpecification;
import org.andante.product.logic.mapper.ProductModelEntityMapper;
import org.andante.product.logic.model.ProductOutput;
import org.andante.product.logic.service.ProductService;
import org.andante.product.logic.specification.ProductSpecificationBuilder;
import org.andante.product.repository.ProductRepository;
import org.andante.product.repository.entity.ProductEntity;
import org.andante.rsql.PersistentRSQLVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;
    private final ProductModelEntityMapper productModelEntityMapper;
    private final RSQLParser rsqlParser;
    private final PersistentRSQLVisitor<ProductEntity> rsqlVisitor;
    private final ProductSpecificationBuilder specificationBuilder;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<ProductOutput> getProducts(Set<Long> identifiers) {
        List<ProductEntity> databaseResponse = productRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(productModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<ProductOutput> getObservedProducts(String username) {
        Set<ProductEntity> databaseResponse = productRepository.findAllByObserver(username);

        return databaseResponse.stream()
                .map(productModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<ProductOutput> getByQuery(ProductQuerySpecification productQuerySpecification, Double minimumRating) {
        Node rootNode = rsqlParser.parse(productQuerySpecification.getQuery());
        Specification<ProductEntity> specification = rootNode.accept(rsqlVisitor);

        Pageable pageSpecification = specificationBuilder.getPageSpecification(productQuerySpecification);

        Set<Long> ids = productRepository.findByMinimumRating(minimumRating);

        specification = specification.and((root, query, criteriaBuilder) -> root.get("id").in(ids));

        Page<ProductEntity> databaseResponse = productRepository.findAll(specification, pageSpecification);

        return databaseResponse.map(productModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ProductOutput> getByObserversCountDescending(Integer page, Integer count) {
        List<Long> databaseResponse = productRepository.findTopIdsByObserversCount(PageRequest.of(page, count));

        List<ProductEntity> observedProducts = productRepository.findAllById(databaseResponse);

        return observedProducts.stream()
                .map(productModelEntityMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OperationStatus changeObservationStatus(String observer, Long identifier) {
        Optional<ProductEntity> databaseResponse = productRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            return OperationStatus.NOT_FOUND;
        }

        ProductEntity observedProduct = databaseResponse.get();

        if (observedProduct.getObservers().contains(observer)) {
            observedProduct.getObservers().remove(observer);
        } else {
            observedProduct.getObservers().add(observer);
        }

        productRepository.save(observedProduct);

        return OperationStatus.OK;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ProductOutput> getByAverageRatingDescending(Integer page, Integer pageSize) {
        Pageable pageRequest = PageRequest.of(page, pageSize);

        List<Long> ids = productRepository.getTopIdsByAverageRating(pageRequest);

        List<ProductEntity> databaseResponse = productRepository.findAllById(ids);

        return databaseResponse.stream()
                .map(productModelEntityMapper::toModel)
                .collect(Collectors.toList());
    }
}
