package com.micuota.controller;

import com.micuota.entity.TeacherOnboarding;
import com.micuota.entity.User;
import com.micuota.repository.TeacherOnboardingRepository;
import com.micuota.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/onboarding")
public class TeacherOnboardingController {

    @Autowired
    private TeacherOnboardingRepository teacherOnboardingRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/teacher")
    public Map<String, Object> getTeacherOnboarding(Authentication authentication) {
        User user = getAuthenticatedTeacher(authentication);
        Optional<TeacherOnboarding> onboarding = teacherOnboardingRepository.findByUser_Id(user.getId());
        return onboarding.map(this::toResponse).orElseGet(() -> emptyResponse(user));
    }

    @PostMapping("/teacher")
    public Map<String, Object> upsertTeacherOnboarding(@RequestBody Map<String, Object> request, Authentication authentication) {
        User user = getAuthenticatedTeacher(authentication);

        TeacherOnboarding onboarding = teacherOnboardingRepository.findByUser_Id(user.getId())
            .orElseGet(() -> {
                TeacherOnboarding created = new TeacherOnboarding();
                created.setUser(user);
                return created;
            });

        if (request.containsKey("headline")) {
            onboarding.setHeadline(asString(request.get("headline")));
        }
        if (request.containsKey("bio")) {
            onboarding.setBio(asString(request.get("bio")));
        }
        if (request.containsKey("teachingLevel")) {
            onboarding.setTeachingLevel(asString(request.get("teachingLevel")));
        }
        if (request.containsKey("specialties")) {
            onboarding.setSpecialties(asString(request.get("specialties")));
        }
        if (request.containsKey("yearsExperience")) {
            onboarding.setYearsExperience(asInteger(request.get("yearsExperience")));
        }
        if (request.containsKey("onboardingComplete")) {
            onboarding.setOnboardingComplete(asBoolean(request.get("onboardingComplete")));
        }

        TeacherOnboarding saved = teacherOnboardingRepository.save(onboarding);
        return toResponse(saved);
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

    private Map<String, Object> toResponse(TeacherOnboarding onboarding) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", onboarding.getId());
        response.put("userId", onboarding.getUser() != null ? onboarding.getUser().getId() : null);
        response.put("headline", onboarding.getHeadline());
        response.put("bio", onboarding.getBio());
        response.put("teachingLevel", onboarding.getTeachingLevel());
        response.put("specialties", onboarding.getSpecialties());
        response.put("yearsExperience", onboarding.getYearsExperience());
        response.put("onboardingComplete", onboarding.getOnboardingComplete());
        return response;
    }

    private Map<String, Object> emptyResponse(User user) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", null);
        response.put("userId", user.getId());
        response.put("headline", null);
        response.put("bio", null);
        response.put("teachingLevel", null);
        response.put("specialties", null);
        response.put("yearsExperience", null);
        response.put("onboardingComplete", Boolean.FALSE);
        return response;
    }

    private String asString(Object value) {
        if (value == null) {
            return null;
        }
        String stringValue = value.toString().trim();
        return stringValue.isEmpty() ? null : stringValue;
    }

    private Integer asInteger(Object value) {
        if (value == null) {
            return null;
        }
        try {
            String stringValue = value.toString().trim();
            return stringValue.isEmpty() ? null : Integer.valueOf(stringValue);
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo yearsExperience debe ser numérico");
        }
    }

    private Boolean asBoolean(Object value) {
        if (value == null) {
            return null;
        }
        String stringValue = value.toString().trim();
        if (stringValue.isEmpty()) {
            return null;
        }
        if ("true".equalsIgnoreCase(stringValue) || "1".equals(stringValue)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(stringValue) || "0".equals(stringValue)) {
            return Boolean.FALSE;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo onboardingComplete debe ser true o false");
    }
}
