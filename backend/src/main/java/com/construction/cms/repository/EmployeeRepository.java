package com.construction.cms.repository;

import com.construction.cms.model.Employee;
import com.construction.cms.model.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByProfession(String profession);
    List<Employee> findByStatus(EmployeeStatus status);
    List<Employee> findByProfessionAndStatus(String profession, EmployeeStatus status);
}
