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

    @GetMapping
    public List<Payment> getPayments(Authentication authentication) {
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")).getId();
        return paymentRepository.findBySubscription_Plan_User_Id(userId);
    }
}
