package org.andante.product.logic.service;

import org.andante.product.logic.model.Producer;
import org.andante.product.logic.model.ProductOutput;

import java.util.List;
import java.util.Set;

public interface ProducerService {

    Set<Producer> getAllById(Set<String> names);
    Set<ProductOutput> getAllProducts(String name);
    List<Producer> getBiggestProducers(Integer page, Integer pageSize);
    Producer create(Producer producer);
    Producer modify(Producer producer);
    Producer delete(String name);
}
