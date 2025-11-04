package com.micuota.controller;

import com.micuota.entity.Plan;
import com.micuota.entity.User;
import com.micuota.repository.PlanRepository;
import com.micuota.repository.UserRepository;
import com.micuota.service.MercadoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    @Value("${ALLOW_DEMO_SEED:false}")
    private boolean allowDemoSeed;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private MercadoPagoService mpService;

    /**
     * Create demo users and a sample plan (course).
     * Returns emails/passwords and created plan id.
     */
    @PostMapping("/seed")
    public Map<String, Object> seedDemoData() {
        // Only allow demo seed when running in 'dev' profile or when ALLOW_DEMO_SEED=true
        boolean devActive = activeProfiles != null && activeProfiles.contains("dev");
        if (!devActive && !allowDemoSeed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Demo seed not allowed in this environment");
        }
        Map<String, Object> out = new HashMap<>();

        // Create professor
        String profEmail = "profesor@local.test";
        String profPass = "profesor";
        com.micuota.entity.User prof = userRepository.findByEmail(profEmail).orElseGet(() -> {
            com.micuota.entity.User u = new com.micuota.entity.User();
            u.setName("Profesor Demo");
            u.setEmail(profEmail);
            u.setPassword(passwordEncoder.encode(profPass));
            u.setRole(com.micuota.entity.User.Role.PROFESOR);
            return userRepository.save(u);
        });

        // Create student
        String studentEmail = "alumno@local.test";
        String studentPass = "alumno";
        com.micuota.entity.User student = userRepository.findByEmail(studentEmail).orElseGet(() -> {
            com.micuota.entity.User u = new com.micuota.entity.User();
            u.setName("Alumno Demo");
            u.setEmail(studentEmail);
            u.setPassword(passwordEncoder.encode(studentPass));
            u.setRole(com.micuota.entity.User.Role.ALUMNO);
            return userRepository.save(u);
        });

        // Create a sample plan/course for the professor
        Plan plan = new Plan();
        plan.setUser(prof);
        plan.setTitle("Clase demo: Taller rápido");
        plan.setPrice(new BigDecimal("10.00"));
        plan.setFrequency("once");
        String mpPlanId = mpService.createPlan(plan.getTitle(), plan.getPrice(), plan.getFrequency());
        plan.setMpPlanId(mpPlanId);
        plan = planRepository.save(plan);

        // Return useful info for demo
        out.put("professor_email", profEmail);
        out.put("professor_password", profPass);
        out.put("student_email", studentEmail);
        out.put("student_password", studentPass);
        out.put("plan_id", plan.getId());
        out.put("plan_mp_id", plan.getMpPlanId());

        return out;
    }
}
