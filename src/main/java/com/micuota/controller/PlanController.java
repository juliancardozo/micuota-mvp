package com.micuota.controller;

import com.micuota.entity.Plan;
import com.micuota.entity.User;
import com.micuota.repository.PlanRepository;
import com.micuota.repository.UserRepository;
import com.micuota.service.MercadoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plans")
public class PlanController {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MercadoPagoService mpService;

    @GetMapping
    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    @PostMapping
    public Plan createPlan(@RequestBody Map<String, String> request, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        String title = request.get("title");
        BigDecimal price = new BigDecimal(request.get("price"));
        String frequency = request.get("frequency");

        Plan plan = new Plan();
        plan.setUser(user);
        plan.setTitle(title);
        plan.setPrice(price);
        plan.setFrequency(frequency);

        // call MercadoPago service to create preapproval plan
        String mpPlanId = mpService.createPlan(title, price, frequency);
        plan.setMpPlanId(mpPlanId);
        return planRepository.save(plan);
    }

    @GetMapping("/{id}")
    public Plan getPlan(@PathVariable Long id) {
        return planRepository.findById(id).orElseThrow(() -> new RuntimeException("Plan not found"));
    }
}
