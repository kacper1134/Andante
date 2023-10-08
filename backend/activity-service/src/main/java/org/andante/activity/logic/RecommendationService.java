package org.andante.activity.logic;

import org.andante.enums.OperationType;
import org.andante.product.dto.ProductOutputDTO;

public interface RecommendationService {
    Void synchronizeProductsCatalog(ProductOutputDTO productOutputDTO, OperationType operationType);
}
