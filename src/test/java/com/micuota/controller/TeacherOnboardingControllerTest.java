package com.micuota.controller;

import com.micuota.entity.TeacherOnboarding;
import com.micuota.entity.User;
import com.micuota.repository.TeacherOnboardingRepository;
import com.micuota.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TeacherOnboardingControllerTest {

    private TeacherOnboardingRepository teacherOnboardingRepository;
    private UserRepository userRepository;
    private TeacherOnboardingController controller;
    private Authentication authentication;
    private User professor;

    @BeforeEach
    void setUp() {
        teacherOnboardingRepository = mock(TeacherOnboardingRepository.class);
        userRepository = mock(UserRepository.class);
        controller = new TeacherOnboardingController(teacherOnboardingRepository, userRepository);
        authentication = mock(Authentication.class);

        professor = new User();
        professor.setId(42L);
        professor.setEmail("profesor@example.com");
        professor.setRole(User.Role.PROFESOR);

        when(authentication.getName()).thenReturn(professor.getEmail());
        when(userRepository.findByEmail(professor.getEmail())).thenReturn(Optional.of(professor));
    }

    @Test
    void getTeacherOnboardingReturnsExistingData() {
        TeacherOnboarding onboarding = new TeacherOnboarding();
        onboarding.setId(5L);
        onboarding.setUser(professor);
        onboarding.setHeadline("Hola mundo");
        onboarding.setBio("Soy profesor de matemáticas");
        onboarding.setTeachingLevel("Secundaria");
        onboarding.setSpecialties("Álgebra");
        onboarding.setYearsExperience(8);
        onboarding.setOnboardingComplete(Boolean.TRUE);

        when(teacherOnboardingRepository.findByUser_Id(professor.getId())).thenReturn(Optional.of(onboarding));

        Map<String, Object> response = controller.getTeacherOnboarding(authentication);

        assertThat(response).containsEntry("id", onboarding.getId())
            .containsEntry("userId", professor.getId())
            .containsEntry("headline", onboarding.getHeadline())
            .containsEntry("bio", onboarding.getBio())
            .containsEntry("teachingLevel", onboarding.getTeachingLevel())
            .containsEntry("specialties", onboarding.getSpecialties())
            .containsEntry("yearsExperience", onboarding.getYearsExperience())
            .containsEntry("onboardingComplete", onboarding.getOnboardingComplete());
    }

    @Test
    void getTeacherOnboardingReturnsEmptyResponseWhenAbsent() {
        when(teacherOnboardingRepository.findByUser_Id(professor.getId())).thenReturn(Optional.empty());

        Map<String, Object> response = controller.getTeacherOnboarding(authentication);

        assertThat(response).containsEntry("id", null)
            .containsEntry("userId", professor.getId())
            .containsEntry("headline", null)
            .containsEntry("bio", null)
            .containsEntry("teachingLevel", null)
            .containsEntry("specialties", null)
            .containsEntry("yearsExperience", null)
            .containsEntry("onboardingComplete", Boolean.FALSE);
    }

    @Test
    void upsertTeacherOnboardingCreatesNewRecordWhenMissing() {
        when(teacherOnboardingRepository.findByUser_Id(professor.getId())).thenReturn(Optional.empty());
        when(teacherOnboardingRepository.save(org.mockito.Mockito.any()))
            .thenAnswer(invocation -> {
                TeacherOnboarding saved = invocation.getArgument(0);
                saved.setId(77L);
                return saved;
            });

        Map<String, Object> request = new HashMap<>();
        request.put("headline", "Profesora apasionada");
        request.put("bio", "Me encanta enseñar ciencias");
        request.put("teachingLevel", "Primaria");
        request.put("specialties", "Ciencias naturales");
        request.put("yearsExperience", 3);
        request.put("onboardingComplete", true);

        Map<String, Object> response = controller.upsertTeacherOnboarding(request, authentication);

        ArgumentCaptor<TeacherOnboarding> captor = ArgumentCaptor.forClass(TeacherOnboarding.class);
        verify(teacherOnboardingRepository).save(captor.capture());

        TeacherOnboarding persisted = captor.getValue();
        assertThat(persisted.getUser()).isEqualTo(professor);
        assertThat(persisted.getHeadline()).isEqualTo(request.get("headline"));
        assertThat(persisted.getBio()).isEqualTo(request.get("bio"));
        assertThat(persisted.getTeachingLevel()).isEqualTo(request.get("teachingLevel"));
        assertThat(persisted.getSpecialties()).isEqualTo(request.get("specialties"));
        assertThat(persisted.getYearsExperience()).isEqualTo(request.get("yearsExperience"));
        assertThat(persisted.getOnboardingComplete()).isEqualTo(Boolean.TRUE);

        assertThat(response).containsEntry("id", 77L)
            .containsEntry("userId", professor.getId())
            .containsEntry("headline", request.get("headline"))
            .containsEntry("bio", request.get("bio"))
            .containsEntry("teachingLevel", request.get("teachingLevel"))
            .containsEntry("specialties", request.get("specialties"))
            .containsEntry("yearsExperience", request.get("yearsExperience"))
            .containsEntry("onboardingComplete", Boolean.TRUE);
    }

    @Test
    void upsertTeacherOnboardingRejectsNonNumericExperience() {
        when(teacherOnboardingRepository.findByUser_Id(professor.getId())).thenReturn(Optional.empty());

        Map<String, Object> request = new HashMap<>();
        request.put("yearsExperience", "no-numero");

        assertThatThrownBy(() -> controller.upsertTeacherOnboarding(request, authentication))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("yearsExperience");
    }

    @Test
    void getAuthenticatedTeacherRejectsNonProfessor() {
        Authentication studentAuth = mock(Authentication.class);
        when(studentAuth.getName()).thenReturn("alumno@example.com");

        User student = new User();
        student.setId(99L);
        student.setEmail("alumno@example.com");
        student.setRole(User.Role.ALUMNO);

        when(userRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> controller.getTeacherOnboarding(studentAuth))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Solo los profesores");
    }

    @Test
    void upsertTeacherOnboardingRejectsInvalidBoolean() {
        when(teacherOnboardingRepository.findByUser_Id(professor.getId())).thenReturn(Optional.empty());

        Map<String, Object> request = new HashMap<>();
        request.put("onboardingComplete", "not-valid");

        assertThatThrownBy(() -> controller.upsertTeacherOnboarding(request, authentication))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("onboardingComplete");
    }
}
