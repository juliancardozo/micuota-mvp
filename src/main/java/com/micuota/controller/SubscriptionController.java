package com.micuota.controller;

import com.micuota.entity.Plan;
import com.micuota.entity.Subscription;
import com.micuota.entity.User;
import com.micuota.repository.PlanRepository;
import com.micuota.repository.SubscriptionRepository;
import com.micuota.repository.UserRepository;
import com.micuota.service.MercadoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MercadoPagoService mpService;

    @PostMapping
    public Subscription createSubscription(@RequestBody Map<String, String> request, Authentication authentication) {
        Long planId = Long.parseLong(request.get("planId"));
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new RuntimeException("Plan not found"));

        if (authentication == null) {
            throw new RuntimeException("Authentication required");
        }
        String email = authentication.getName();
        User student = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Subscription subscription = new Subscription();
        subscription.setPlan(plan);
        subscription.setStudent(student);

        String mpSubscriptionId = mpService.createSubscription(plan.getMpPlanId());
        subscription.setMpSubscriptionId(mpSubscriptionId);
        subscription.setStatus("pending");
        return subscriptionRepository.save(subscription);
    }
}
