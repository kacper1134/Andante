package org.andante.product.logic.service.impl;

import lombok.RequiredArgsConstructor;
import org.andante.product.exception.ProducerConflictException;
import org.andante.product.exception.ProducerNotFoundException;
import org.andante.product.logic.mapper.ProducerModelEntityMapper;
import org.andante.product.logic.mapper.ProductModelEntityMapper;
import org.andante.product.logic.model.Producer;
import org.andante.product.logic.model.ProductOutput;
import org.andante.product.logic.service.ProducerService;
import org.andante.product.repository.ProducerRepository;
import org.andante.product.repository.entity.ProducerEntity;
import org.andante.product.repository.entity.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DefaultProducerService implements ProducerService {

    private static final String PRODUCER_CONFLICT_EXCEPTION_MESSAGE = "Producer %s already exists";
    private static final String PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE = "Producer %s does not exist";

    private final ProducerRepository producerRepository;
    private final ProducerModelEntityMapper producerModelEntityMapper;
    private final ProductModelEntityMapper productModelEntityMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<Producer> getAllById(Set<String> names) {
        List<ProducerEntity> databaseResponse = producerRepository.findAllById(names);

        return databaseResponse.stream()
                .map(producerModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<ProductOutput> getAllProducts(String name) {
        Optional<ProducerEntity> databaseResponse = producerRepository.findById(name);

        if (databaseResponse.isEmpty()) {
            return Set.of();
        }

        Set<ProductEntity> products = Optional.ofNullable(databaseResponse.get().getProducts()).orElse(Set.of());

        return products.stream()
                .map(productModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Producer> getBiggestProducers(Integer page, Integer size) {
        Pageable pageRequest = PageRequest.of(page, size);

        List<ProducerEntity> databaseResponse = producerRepository.getBiggestProducers(pageRequest);

        return databaseResponse.stream()
                .map(producerModelEntityMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Producer create(Producer producer) {
        if (producer.getName() != null && producerRepository.existsById(producer.getName())) {
            throw new ProducerConflictException(String.format(PRODUCER_CONFLICT_EXCEPTION_MESSAGE, producer.getName()));
        }

        ProducerEntity producerToCreate = producerModelEntityMapper.toEntity(producer);

        ProducerEntity createdProducer = producerRepository.save(producerToCreate);

        return producerModelEntityMapper.toModel(createdProducer);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Producer modify(Producer producer) {
        if (producer.getName() == null || !producerRepository.existsById(producer.getName())) {
            throw new ProducerNotFoundException(String.format(PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE, producer.getName()));
        }

        ProducerEntity producerToModify = producerModelEntityMapper.toEntity(producer);

        ProducerEntity modifiedProducer = producerRepository.save(producerToModify);

        return producerModelEntityMapper.toModel(modifiedProducer);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Producer delete(String name) {
        Optional<ProducerEntity> databaseResponse = producerRepository.findById(name);

        if (databaseResponse.isEmpty()) {
            throw new ProducerNotFoundException(String.format(PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE, name));
        }

        producerRepository.delete(databaseResponse.get());

        return producerModelEntityMapper.toModel(databaseResponse.get());
    }
}
