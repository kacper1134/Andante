package org.andante.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum KafkaConsumerGroup {
    PRODUCT_ORDER_GROUP("product.order.group"),
    ACTIVITY_ORDER_GROUP("activity.order.group"),
    ACTIVITY_PRODUCT_GROUP("activity.product.group");

    private final String name;
}
