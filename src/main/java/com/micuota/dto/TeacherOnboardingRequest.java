package com.micuota.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherOnboardingRequest {

    private String headline;
    private boolean headlineSet;

    private String bio;
    private boolean bioSet;

    private String teachingLevel;
    private boolean teachingLevelSet;

    private String specialties;
    private boolean specialtiesSet;

    private Integer yearsExperience;
    private boolean yearsExperienceSet;

    private Boolean onboardingComplete;
    private boolean onboardingCompleteSet;

    @Size(max = 120, message = "headline no debe exceder 120 caracteres")
    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headlineSet = true;
        this.headline = sanitize(headline);
    }

    public boolean isHeadlineSet() {
        return headlineSet;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bioSet = true;
        this.bio = sanitize(bio);
    }

    public boolean isBioSet() {
        return bioSet;
    }

    @Size(max = 120, message = "teachingLevel no debe exceder 120 caracteres")
    public String getTeachingLevel() {
        return teachingLevel;
    }

    public void setTeachingLevel(String teachingLevel) {
        this.teachingLevelSet = true;
        this.teachingLevel = sanitize(teachingLevel);
    }

    public boolean isTeachingLevelSet() {
        return teachingLevelSet;
    }

    @Size(max = 120, message = "specialties no debe exceder 120 caracteres")
    public String getSpecialties() {
        return specialties;
    }

    public void setSpecialties(String specialties) {
        this.specialtiesSet = true;
        this.specialties = sanitize(specialties);
    }

    public boolean isSpecialtiesSet() {
        return specialtiesSet;
    }

    @PositiveOrZero(message = "yearsExperience debe ser un número positivo")
    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperienceSet = true;
        this.yearsExperience = yearsExperience;
    }

    public boolean isYearsExperienceSet() {
        return yearsExperienceSet;
    }

    public Boolean getOnboardingComplete() {
        return onboardingComplete;
    }

    public void setOnboardingComplete(Boolean onboardingComplete) {
        this.onboardingCompleteSet = true;
        this.onboardingComplete = onboardingComplete;
    }

    public boolean isOnboardingCompleteSet() {
        return onboardingCompleteSet;
    }

    private String sanitize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
