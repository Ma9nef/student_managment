package tn.esprit.studentmanagement.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.studentmanagement.entities.Department;
import tn.esprit.studentmanagement.repositories.DepartmentRepository;
import java.util.List;
import tn.esprit.studentmanagement.DTO.DepartmentDTO;

@Service
public class DepartmentService implements IDepartmentService {
    @Autowired
    DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(d -> new DepartmentDTO(
                        d.getIdDepartment(),
                        d.getName(),
                        d.getLocation(),
                        d.getPhone(),
                        d.getHead()
                ))
                .toList();
    }

    @Override
    public Department getDepartmentById(Long idDepartment) {
        return departmentRepository.findById(idDepartment).get();
    }
    @Override
    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }
    @Override
    public void deleteDepartment(Long idDepartment) {
        departmentRepository.deleteById(idDepartment);
    }
}