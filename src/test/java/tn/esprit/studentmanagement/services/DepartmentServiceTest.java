package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.repositories.DepartmentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllDepartments() {
        Department d1 = new Department();
        Department d2 = new Department();   // ✅ correction ici

        when(departmentRepository.findAll()).thenReturn(Arrays.asList(d1, d2));

        List<Department> result = departmentService.getAllDepartments();

        assertEquals(2, result.size());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void testGetDepartmentById() {
        Department d = new Department();
        d.setIdDepartment(1L);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(d));

        Department result = departmentService.getDepartmentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getIdDepartment());
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveDepartment() {
        Department dep = new Department();
        dep.setName("IT");

        when(departmentRepository.save(dep)).thenReturn(dep);

        Department result = departmentService.saveDepartment(dep);

        assertEquals("IT", result.getName());
        verify(departmentRepository, times(1)).save(dep);
    }

    @Test
    void testDeleteDepartment() {
        departmentService.deleteDepartment(5L);

        verify(departmentRepository, times(1)).deleteById(5L);
    }
}
