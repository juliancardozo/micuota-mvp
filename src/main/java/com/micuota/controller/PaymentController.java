package com.micuota.controller;

import com.micuota.entity.Payment;
import com.micuota.repository.PaymentRepository;
import com.micuota.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.micuota.repository.SubscriptionRepository subscriptionRepository;

    @GetMapping
    public List<Payment> getPayments(Authentication authentication) {
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")).getId();
        return paymentRepository.findBySubscription_Plan_User_Id(userId);
    }

    @org.springframework.web.bind.annotation.PostMapping("/charge")
    public Payment chargeSubscription(@org.springframework.web.bind.annotation.RequestBody java.util.Map<String, String> request, Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("Authentication required");
        }
        String email = authentication.getName();
        com.micuota.entity.User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Long subscriptionId = Long.parseLong(request.get("subscriptionId"));
        com.micuota.entity.Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new RuntimeException("Subscription not found"));

        // Only allow the student to pay for their own subscription (or professor for testing)
        if (!subscription.getStudent().getId().equals(user.getId()) && !subscription.getPlan().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to charge this subscription");
        }

        java.math.BigDecimal amount = subscription.getPlan().getPrice();

        com.micuota.entity.Payment payment = new com.micuota.entity.Payment();
        payment.setSubscription(subscription);
        payment.setAmount(amount);
        payment.setStatus("paid");
        payment.setPaidAt(java.time.LocalDateTime.now());
        payment.setMpPaymentId("mock-payment-" + System.currentTimeMillis());

        // mark subscription active
        subscription.setStatus("active");
        subscriptionRepository.save(subscription);

        return paymentRepository.save(payment);
    }
}
