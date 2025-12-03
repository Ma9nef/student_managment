package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.repositories.EnrollmentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllEnrollments() {
        // Arrange
        Enrollment e1 = new Enrollment();
        Enrollment e2 = new Enrollment();
        when(enrollmentRepository.findAll()).thenReturn(Arrays.asList(e1, e2));

        // Act
        List<Enrollment> result = enrollmentService.getAllEnrollments();

        // Assert
        assertEquals(2, result.size());
        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    void testGetEnrollmentById() {
        // Arrange
        Enrollment e = new Enrollment();
        e.setIdEnrollment(1L);

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(e));

        // Act
        Enrollment result = enrollmentService.getEnrollmentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdEnrollment());
        verify(enrollmentRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveEnrollment() {
        // Arrange
        Enrollment e = new Enrollment();
        e.setGrade(15.0); // ✔ Valeur numérique

        when(enrollmentRepository.save(e)).thenReturn(e);

        // Act
        Enrollment result = enrollmentService.saveEnrollment(e);

        // Assert
        assertNotNull(result);
        assertEquals(15.0, result.getGrade()); // ✔ Comparaison correcte
        verify(enrollmentRepository, times(1)).save(e);
    }

    @Test
    void testDeleteEnrollment() {
        // Act
        enrollmentService.deleteEnrollment(10L);

        // Assert
        verify(enrollmentRepository, times(1)).deleteById(10L);
    }
}
