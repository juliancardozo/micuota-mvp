package com.micuota.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micuota.entity.Payment;
import com.micuota.entity.Subscription;
import com.micuota.repository.PaymentRepository;
import com.micuota.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/webhooks/mercadopago")
public class MPWebhookController {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);
            String event = root.path("type").asText();

            if ("payment".equalsIgnoreCase(event)) {
                String mpSubscriptionId = root.path("data").path("subscription_id").asText();
                BigDecimal amount = root.path("data").path("amount").decimalValue();
                String status = root.path("data").path("status").asText();

                Subscription subscription = subscriptionRepository.findAll().stream()
                    .filter(s -> s.getMpSubscriptionId().equals(mpSubscriptionId))
                    .findFirst()
                    .orElse(null);

                if (subscription != null) {
                    Payment payment = new Payment();
                    payment.setSubscription(subscription);
                    payment.setAmount(amount);
                    payment.setStatus(status);
                    payment.setPaidAt(LocalDateTime.now());
                    payment.setMpPaymentId(root.path("data").path("id").asText());
                    paymentRepository.save(payment);

                    subscription.setStatus(status);
                    subscriptionRepository.save(subscription);
                }
            }
            return ResponseEntity.ok("received");
        } catch (Exception e) {
            return ResponseEntity.ok("error");
        }
    }
}