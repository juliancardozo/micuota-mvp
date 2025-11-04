package com.micuota.controller;

import com.micuota.dto.TeacherOnboardingRequest;
import com.micuota.dto.TeacherOnboardingResponse;
import com.micuota.entity.TeacherOnboarding;
import com.micuota.entity.User;
import com.micuota.repository.TeacherOnboardingRepository;
import com.micuota.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/onboarding")
public class TeacherOnboardingController {

    private final TeacherOnboardingRepository teacherOnboardingRepository;

    private final UserRepository userRepository;

    @Autowired
    public TeacherOnboardingController(TeacherOnboardingRepository teacherOnboardingRepository,
                                       UserRepository userRepository) {
        this.teacherOnboardingRepository = teacherOnboardingRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/teacher")
    public TeacherOnboardingResponse getTeacherOnboarding(Authentication authentication) {
        User user = getAuthenticatedTeacher(authentication);
        Optional<TeacherOnboarding> onboarding = teacherOnboardingRepository.findByUser_Id(user.getId());
        return onboarding.map(TeacherOnboardingResponse::fromEntity)
            .orElseGet(() -> TeacherOnboardingResponse.empty(user));
    }

    @PostMapping("/teacher")
    public TeacherOnboardingResponse upsertTeacherOnboarding(@Valid @RequestBody TeacherOnboardingRequest request,
                                                             Authentication authentication) {
        User user = getAuthenticatedTeacher(authentication);

        TeacherOnboarding onboarding = teacherOnboardingRepository.findByUser_Id(user.getId())
            .orElseGet(() -> {
                TeacherOnboarding created = new TeacherOnboarding();
                created.setUser(user);
                return created;
            });

        applyRequest(onboarding, request);

        TeacherOnboarding saved = teacherOnboardingRepository.save(onboarding);
        return TeacherOnboardingResponse.fromEntity(saved);
    }

    private User getAuthenticatedTeacher(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.getRole() != User.Role.PROFESOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo los profesores pueden acceder al onboarding");
        }
        return user;
    }

    private void applyRequest(TeacherOnboarding onboarding, TeacherOnboardingRequest request) {
        if (request.isHeadlineSet()) {
            onboarding.setHeadline(request.getHeadline());
        }
        if (request.isBioSet()) {
            onboarding.setBio(request.getBio());
        }
        if (request.isTeachingLevelSet()) {
            onboarding.setTeachingLevel(request.getTeachingLevel());
        }
        if (request.isSpecialtiesSet()) {
            onboarding.setSpecialties(request.getSpecialties());
        }
        if (request.isYearsExperienceSet()) {
            onboarding.setYearsExperience(request.getYearsExperience());
        }
        if (request.isOnboardingCompleteSet()) {
            onboarding.setOnboardingComplete(request.getOnboardingComplete());
        }
    }
}
