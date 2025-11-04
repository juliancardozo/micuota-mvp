package com.micuota.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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

    @Value("${mercadopago.base-url:http://wiremock:8080}")
    private String mercadopagoBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String createPlan(String title, BigDecimal price, String frequency) {
        if (isConfiguredForRealAPI()) {
            // TODO: Implement call to MercadoPago API /preapproval_plan
            logger.info("Creating real MercadoPago plan: {} - ${} - {}", title, price, frequency);
            // return callRealMercadoPagoAPI(title, price, frequency);
        }
        // If there's an explicit base URL (WireMock / mock server), call it and return the id
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("title", title);
            body.put("price", price.toString());
            body.put("frequency", frequency);

            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restTemplate.postForObject(mercadopagoBaseUrl + "/v1/plans", body, Map.class);
            if (resp != null && resp.containsKey("id")) {
                String id = String.valueOf(resp.get("id"));
                logger.debug("MP plan created (via mock): {}", id);
                return id;
            }
        } catch (RestClientException ex) {
            logger.warn("Failed to call mock MercadoPago at {} - falling back to in-memory mock: {}", mercadopagoBaseUrl, ex.getMessage());
        }

        // Fallback mock implementation
        String mockPlanId = "fake-plan-id-" + System.currentTimeMillis();
        logger.debug("Fallback mock MercadoPago plan created: {}", mockPlanId);
        return mockPlanId;
    }

    public String createSubscription(String mpPlanId) {
        if (isConfiguredForRealAPI()) {
            // TODO: Implement call to MercadoPago API /preapproval
            logger.info("Creating real MercadoPago subscription for plan: {}", mpPlanId);
            // return callRealMercadoPagoSubscriptionAPI(mpPlanId);
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("plan_id", mpPlanId);

            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restTemplate.postForObject(mercadopagoBaseUrl + "/v1/subscriptions", body, Map.class);
            if (resp != null && resp.containsKey("id")) {
                String id = String.valueOf(resp.get("id"));
                logger.debug("MP subscription created (via mock): {}", id);
                return id;
            }
        } catch (RestClientException ex) {
            logger.warn("Failed to call mock MercadoPago at {} - falling back to in-memory mock: {}", mercadopagoBaseUrl, ex.getMessage());
        }

        String mockSubscriptionId = "fake-subscription-id-" + System.currentTimeMillis();
        logger.debug("Fallback mock MercadoPago subscription created: {}", mockSubscriptionId);
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
