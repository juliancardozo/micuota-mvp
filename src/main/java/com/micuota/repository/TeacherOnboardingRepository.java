package com.micuota.repository;

import com.micuota.entity.TeacherOnboarding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherOnboardingRepository extends JpaRepository<TeacherOnboarding, Long> {
    Optional<TeacherOnboarding> findByUser_Id(Long userId);
}
