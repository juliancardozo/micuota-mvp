package com.micuota.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.micuota.entity.TeacherOnboarding;
import com.micuota.entity.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TeacherOnboardingResponse(
    Long id,
    Long userId,
    String headline,
    String bio,
    String teachingLevel,
    String specialties,
    Integer yearsExperience,
    Boolean onboardingComplete
) {
    public static TeacherOnboardingResponse fromEntity(TeacherOnboarding onboarding) {
        Long userId = onboarding.getUser() != null ? onboarding.getUser().getId() : null;
        return new TeacherOnboardingResponse(
            onboarding.getId(),
            userId,
            onboarding.getHeadline(),
            onboarding.getBio(),
            onboarding.getTeachingLevel(),
            onboarding.getSpecialties(),
            onboarding.getYearsExperience(),
            onboarding.getOnboardingComplete()
        );
    }

    public static TeacherOnboardingResponse empty(User user) {
        return new TeacherOnboardingResponse(
            null,
            user.getId(),
            null,
            null,
            null,
            null,
            null,
            Boolean.FALSE
        );
    }
}
