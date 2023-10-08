package org.andante.orders;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RequestMapping("/order/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> startPayment(@RequestParam("orderId") Long orderId) {
        return ResponseEntity.ok(paymentService.createPayment(orderId));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> stripeWebhookEndpoint(@RequestBody String json, HttpServletRequest request) {
        return ResponseEntity.ok(paymentService.finishPayment(json, request));
    }
}
