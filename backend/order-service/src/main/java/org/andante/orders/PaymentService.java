package org.andante.orders;

import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.andante.orders.enums.OrderStatus;
import org.andante.orders.logic.model.OrderInput;
import org.andante.orders.logic.model.OrderOutput;
import org.andante.orders.logic.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PaymentService {

    private static final String STRIPE_HEADER = "Stripe-Signature";
    private static final String STRIPE_SUCCEEDED_INTENT =  "payment_intent.succeeded";
    private static final String ORDER_ID = "orderId";
    private final OrderService orderService;
    @Value("${stripe.private_key}")
    private String privateKey;

    @Value("${stripe.webhook_secret}")
    private String webhookSecret;

    public String createPayment(Long orderId) {
        Optional<OrderOutput> order = orderService.getById(orderId);
        return createPaymentIntent(calculateOrderAmount(order.get()), getOrderCurrency(), order.get());
    }

    @SneakyThrows
    private String createPaymentIntent(Long amount, String currency, OrderOutput order) {
        Stripe.apiKey=privateKey;
        Map<String, String> metadata = new HashMap<>();
        metadata.put(ORDER_ID, order.getId().toString());

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .putAllMetadata(metadata)
                .setAmount(amount)
                .setCurrency(currency)
                .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods
                        .builder()
                        .setEnabled(true)
                        .build())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent.getClientSecret();
    }

    @SneakyThrows
    // TODO Handle new order
    public String finishPayment(String json, HttpServletRequest request) {
        String sigHeader = request.getHeader(STRIPE_HEADER);
        String endpointSecret = webhookSecret;
        Event event = Webhook.constructEvent(json, sigHeader, endpointSecret);

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        Optional<StripeObject> deserializedObject = dataObjectDeserializer.getObject();

        if (deserializedObject.isPresent()) {
            stripeObject = deserializedObject.get();
        }

        if (STRIPE_SUCCEEDED_INTENT.equals(event.getType())) {
            PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
            if(paymentIntent != null) {
                Long orderId = Long.parseLong(paymentIntent.getMetadata().get(ORDER_ID));
                Optional<OrderOutput> order = orderService.getById(orderId);
                order.ifPresent(this::finishOrder);
            }
        }

        return "";
    }

    // TODO Handle order not exists
    private Long calculateOrderAmount(OrderOutput order) {
        return order.getTotalPrice().multiply(BigDecimal.valueOf(100)).longValue();
    }

    private String getOrderCurrency() {
        return "pln";
    }

    private void finishOrder(OrderOutput order) {
        OrderInput orderInput = order.toOrderInput();
        orderInput.setStatus(OrderStatus.COMPLETED);
        orderService.update(orderInput);
    }
}
