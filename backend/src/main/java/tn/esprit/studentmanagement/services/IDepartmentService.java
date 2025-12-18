package tn.esprit.studentmanagement.services;

import tn.esprit.studentmanagement.DTO.DepartmentDTO;
import tn.esprit.studentmanagement.entities.Department;

import java.util.List;

public interface IDepartmentService {

    List<DepartmentDTO> getAllDepartments();

    Department getDepartmentById(Long idDepartment);

    Department saveDepartment(Department department);

    void deleteDepartment(Long idDepartment);
}
