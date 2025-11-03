package com.micuota.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@Service
public class MercadoPagoService {
    
    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoService.class);
    
    @Value("${mercadopago.access-token:}")
    private String accessToken;
    
    @Value("${mercadopago.client-id:}")
    private String clientId;
    
    @Value("${mercadopago.client-secret:}")
    private String clientSecret;
    
    @Value("${mercadopago.webhook-secret:}")
    private String webhookSecret;
    
    @Value("${mercadopago.sandbox:true}")
    private boolean sandbox;

    public String createPlan(String title, BigDecimal price, String frequency) {
        if (isConfiguredForRealAPI()) {
            // TODO: Implement call to MercadoPago API /preapproval_plan
            logger.info("Creating real MercadoPago plan: {} - ${} - {}", title, price, frequency);
            // return callRealMercadoPagoAPI(title, price, frequency);
        }
        
        // Mock implementation for development
        String mockPlanId = "fake-plan-id-" + System.currentTimeMillis();
        logger.debug("Mock MercadoPago plan created: {}", mockPlanId);
        return mockPlanId;
    }

    public String createSubscription(String mpPlanId) {
        if (isConfiguredForRealAPI()) {
            // TODO: Implement call to MercadoPago API /preapproval
            logger.info("Creating real MercadoPago subscription for plan: {}", mpPlanId);
            // return callRealMercadoPagoSubscriptionAPI(mpPlanId);
        }
        
        // Mock implementation for development
        String mockSubscriptionId = "fake-subscription-id-" + System.currentTimeMillis();
        logger.debug("Mock MercadoPago subscription created: {}", mockSubscriptionId);
        return mockSubscriptionId;
    }
    
    /**
     * Check if MercadoPago is properly configured for real API calls
     */
    private boolean isConfiguredForRealAPI() {
        boolean configured = accessToken != null && !accessToken.isEmpty() 
                           && clientId != null && !clientId.isEmpty()
                           && clientSecret != null && !clientSecret.isEmpty();
        
        if (!configured) {
            logger.warn("MercadoPago not fully configured. Using mock implementation. " +
                       "Set MERCADOPAGO_ACCESS_TOKEN, MERCADOPAGO_CLIENT_ID, and MERCADOPAGO_CLIENT_SECRET environment variables.");
        }
        
        return configured;
    }
    
    /**
     * Validate webhook signature (for future implementation)
     */
    public boolean validateWebhookSignature(String signature, String payload) {
        if (webhookSecret == null || webhookSecret.isEmpty()) {
            logger.warn("Webhook secret not configured. Cannot validate signature.");
            return false;
        }
        
        // TODO: Implement real signature validation
        logger.debug("Validating webhook signature (mock implementation)");
        return true;
    }
    
    /**
     * Get the base URL for MercadoPago API based on sandbox setting
     */
    public String getApiBaseUrl() {
        return sandbox ? "https://api.mercadopago.com" : "https://api.mercadopago.com";
    }
}
