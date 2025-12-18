package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllStudents() {
        // Arrange
        Student s1 = new Student();
        Student s2 = new Student();
        when(studentRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        // Act
        List<Student> result = studentService.getAllStudents();

        // Assert
        assertEquals(2, result.size());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void testGetStudentById() {
        // Arrange
        Student student = new Student();
        student.setIdStudent(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // Act
        Student result = studentService.getStudentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdStudent());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveStudent() {
        // Arrange
        Student student = new Student();
        student.setFirstName("John");
        student.setLastName("doe");

        when(studentRepository.save(student)).thenReturn(student);

        // Act
        Student result = studentService.saveStudent(student);

        // Assert
        assertEquals("John", result.getFirstName());
        assertEquals("doe", result.getLastName());
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void testDeleteStudent() {
        // Act
        studentService.deleteStudent(10L);

        // Assert
        verify(studentRepository, times(1)).deleteById(10L);
    }
}
