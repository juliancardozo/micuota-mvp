package com.micuota.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "teacher_onboarding")
@Getter
@Setter
@NoArgsConstructor
public class TeacherOnboarding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 120)
    private String headline;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 120)
    private String teachingLevel;

    @Column(length = 120)
    private String specialties;

    private Integer yearsExperience;

    private Boolean onboardingComplete = Boolean.FALSE;
}
