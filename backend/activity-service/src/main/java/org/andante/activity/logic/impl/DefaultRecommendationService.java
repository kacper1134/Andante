package org.andante.activity.logic.impl;

import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.DeleteItem;
import com.recombee.api_client.api_requests.SetItemValues;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.andante.activity.configuration.ActivityConfiguration;
import org.andante.activity.logic.RecommendationService;
import org.andante.enums.OperationType;
import org.andante.product.dto.ProductOutputDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DefaultRecommendationService implements RecommendationService {
    @Override
    public Void synchronizeProductsCatalog(ProductOutputDTO productOutputDTO, OperationType operationType) {
        if(operationType.equals(OperationType.DELETE)) {
            return removeProductFromCatalog(productOutputDTO);
        }
        else {
            return saveProductInCatalog(productOutputDTO);
        }
    }

    private final ActivityConfiguration activityConfiguration;

    @SneakyThrows
    private Void saveProductInCatalog(ProductOutputDTO productOutputDTO) {
        RecombeeClient client = activityConfiguration.recombeeClient();
        client.send(new SetItemValues(productOutputDTO.getId().toString(),
                new HashMap<>() {{
                    put("name", productOutputDTO.getName());
                    put("description", productOutputDTO.getDescription());
                    put("price", productOutputDTO.getPrice());
                    put("category", productOutputDTO.getProductType().toString());
                }}).setCascadeCreate(true));
        return null;
    }

    @SneakyThrows
    private Void removeProductFromCatalog(ProductOutputDTO productOutputDTO) {
        RecombeeClient client = activityConfiguration.recombeeClient();
        client.send(new DeleteItem(productOutputDTO.getId().toString()));
        return null;
    }
}
