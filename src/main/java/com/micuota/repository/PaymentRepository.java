package com.micuota.repository;

import com.micuota.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findBySubscription_Plan_User_Id(Long userId);
}
