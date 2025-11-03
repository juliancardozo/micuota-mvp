package com.micuota.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private BigDecimal price;

    private String frequency;

    @Column(name = "mp_plan_id")
    private String mpPlanId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
