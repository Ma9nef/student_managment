package tn.esprit.studentmanagement.DTO;

public record DepartmentDTO(
        Long idDepartment,
        String name,
        String location,
        String phone,
        String head
) {}
