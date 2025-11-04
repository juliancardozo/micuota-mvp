package com.micuota.controller;

import com.micuota.dto.TeacherOnboardingRequest;
import com.micuota.dto.TeacherOnboardingResponse;
import com.micuota.entity.TeacherOnboarding;
import com.micuota.entity.User;
import com.micuota.repository.TeacherOnboardingRepository;
import com.micuota.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

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

    private Validator validator;

    @BeforeEach
    void setUp() {
        teacherOnboardingRepository = mock(TeacherOnboardingRepository.class);
        userRepository = mock(UserRepository.class);
        controller = new TeacherOnboardingController(teacherOnboardingRepository, userRepository);
        authentication = mock(Authentication.class);
        validator = Validation.buildDefaultValidatorFactory().getValidator();

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

        TeacherOnboardingResponse response = controller.getTeacherOnboarding(authentication);

        assertThat(response.id()).isEqualTo(onboarding.getId());
        assertThat(response.userId()).isEqualTo(professor.getId());
        assertThat(response.headline()).isEqualTo(onboarding.getHeadline());
        assertThat(response.bio()).isEqualTo(onboarding.getBio());
        assertThat(response.teachingLevel()).isEqualTo(onboarding.getTeachingLevel());
        assertThat(response.specialties()).isEqualTo(onboarding.getSpecialties());
        assertThat(response.yearsExperience()).isEqualTo(onboarding.getYearsExperience());
        assertThat(response.onboardingComplete()).isEqualTo(onboarding.getOnboardingComplete());
    }

    @Test
    void getTeacherOnboardingReturnsEmptyResponseWhenAbsent() {
        when(teacherOnboardingRepository.findByUser_Id(professor.getId())).thenReturn(Optional.empty());

        TeacherOnboardingResponse response = controller.getTeacherOnboarding(authentication);

        assertThat(response.id()).isNull();
        assertThat(response.userId()).isEqualTo(professor.getId());
        assertThat(response.headline()).isNull();
        assertThat(response.bio()).isNull();
        assertThat(response.teachingLevel()).isNull();
        assertThat(response.specialties()).isNull();
        assertThat(response.yearsExperience()).isNull();
        assertThat(response.onboardingComplete()).isFalse();
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

        TeacherOnboardingRequest request = new TeacherOnboardingRequest();
        request.setHeadline("Profesora apasionada");
        request.setBio("Me encanta enseñar ciencias");
        request.setTeachingLevel("Primaria");
        request.setSpecialties("Ciencias naturales");
        request.setYearsExperience(3);
        request.setOnboardingComplete(true);

        TeacherOnboardingResponse response = controller.upsertTeacherOnboarding(request, authentication);

        ArgumentCaptor<TeacherOnboarding> captor = ArgumentCaptor.forClass(TeacherOnboarding.class);
        verify(teacherOnboardingRepository).save(captor.capture());

        TeacherOnboarding persisted = captor.getValue();
        assertThat(persisted.getUser()).isEqualTo(professor);
        assertThat(persisted.getHeadline()).isEqualTo(request.getHeadline());
        assertThat(persisted.getBio()).isEqualTo(request.getBio());
        assertThat(persisted.getTeachingLevel()).isEqualTo(request.getTeachingLevel());
        assertThat(persisted.getSpecialties()).isEqualTo(request.getSpecialties());
        assertThat(persisted.getYearsExperience()).isEqualTo(request.getYearsExperience());
        assertThat(persisted.getOnboardingComplete()).isTrue();

        assertThat(response.id()).isEqualTo(77L);
        assertThat(response.userId()).isEqualTo(professor.getId());
        assertThat(response.headline()).isEqualTo(request.getHeadline());
        assertThat(response.bio()).isEqualTo(request.getBio());
        assertThat(response.teachingLevel()).isEqualTo(request.getTeachingLevel());
        assertThat(response.specialties()).isEqualTo(request.getSpecialties());
        assertThat(response.yearsExperience()).isEqualTo(request.getYearsExperience());
        assertThat(response.onboardingComplete()).isTrue();
    }

    @Test
    void upsertTeacherOnboardingOnlyUpdatesProvidedFields() {
        TeacherOnboarding existing = new TeacherOnboarding();
        existing.setId(12L);
        existing.setUser(professor);
        existing.setHeadline("Existente");
        existing.setBio("Bio original");
        existing.setTeachingLevel("Secundaria");
        existing.setSpecialties("Matemática");
        existing.setYearsExperience(10);
        existing.setOnboardingComplete(Boolean.FALSE);

        when(teacherOnboardingRepository.findByUser_Id(professor.getId())).thenReturn(Optional.of(existing));
        when(teacherOnboardingRepository.save(existing)).thenReturn(existing);

        TeacherOnboardingRequest request = new TeacherOnboardingRequest();
        request.setBio("Bio actualizada");
        request.setOnboardingComplete(true);

        TeacherOnboardingResponse response = controller.upsertTeacherOnboarding(request, authentication);

        assertThat(existing.getHeadline()).isEqualTo("Existente");
        assertThat(existing.getBio()).isEqualTo("Bio actualizada");
        assertThat(existing.getTeachingLevel()).isEqualTo("Secundaria");
        assertThat(existing.getSpecialties()).isEqualTo("Matemática");
        assertThat(existing.getYearsExperience()).isEqualTo(10);
        assertThat(existing.getOnboardingComplete()).isTrue();

        assertThat(response.bio()).isEqualTo("Bio actualizada");
        assertThat(response.onboardingComplete()).isTrue();
    }

    @Test
    void upsertTeacherOnboardingTrimsBlankStringsToNull() {
        TeacherOnboarding existing = new TeacherOnboarding();
        existing.setId(55L);
        existing.setUser(professor);

        when(teacherOnboardingRepository.findByUser_Id(professor.getId())).thenReturn(Optional.of(existing));
        when(teacherOnboardingRepository.save(existing)).thenReturn(existing);

        TeacherOnboardingRequest request = new TeacherOnboardingRequest();
        request.setHeadline("   ");
        request.setBio("   texto con espacios   ");

        controller.upsertTeacherOnboarding(request, authentication);

        assertThat(existing.getHeadline()).isNull();
        assertThat(existing.getBio()).isEqualTo("texto con espacios");
    }

    @Test
    void requestValidationFailsForNegativeExperience() {
        TeacherOnboardingRequest request = new TeacherOnboardingRequest();
        request.setYearsExperience(-1);

        Set<ConstraintViolation<TeacherOnboardingRequest>> violations = validator.validate(request);

        assertThat(violations).anySatisfy(violation ->
            assertThat(violation.getPropertyPath().toString()).isEqualTo("yearsExperience"));
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
}
