package com.example.baseb.common.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

        Optional<Employee> findByEmail(String email);

        boolean existsByEmail(String email);

        List<Employee> findByDepartmentCode(String departmentCode);

        @org.springframework.data.jpa.repository.Query("SELECT e FROM Employee e " +
                        "LEFT JOIN e.department d " +
                        "WHERE LOWER(e.id) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "OR LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        List<Employee> searchByKeyword(@org.springframework.data.repository.query.Param("keyword") String keyword);

        @org.springframework.data.jpa.repository.Query("SELECT e FROM Employee e " +
                        "LEFT JOIN FETCH e.employeeRoles er " +
                        "LEFT JOIN FETCH er.role r " +
                        "LEFT JOIN FETCH r.roleAuthorities ra " +
                        "LEFT JOIN FETCH ra.authority " +
                        "WHERE e.id = :id")
        Optional<Employee> findByUsernameWithRoles(
                        @org.springframework.data.repository.query.Param("id") String id);
}
