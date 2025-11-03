package com.micuota.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MercadoPagoService {

    // In a real implementation, use MercadoPago SDK or HTTP calls
    // Here we stub the method to return a fake id
    public String createPlan(String title, BigDecimal price, String frequency) {
        // TODO: Implement call to MercadoPago API /preapproval_plan
        return "fake-plan-id-" + System.currentTimeMillis();
    }

    public String createSubscription(String mpPlanId) {
        // TODO: Implement call to MercadoPago API /preapproval
        return "fake-subscription-id-" + System.currentTimeMillis();
    }
}
