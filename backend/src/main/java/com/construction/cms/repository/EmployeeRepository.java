package com.construction.cms.repository;

import com.construction.cms.model.Employee;
import com.construction.cms.model.EmployeeStatus;
import com.construction.cms.model.Profession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.currentProject")
    List<Employee> findAllWithCurrentProject();

    @Query("SELECT e FROM Employee e WHERE e.endDate IS NOT NULL AND e.endDate <= :today AND e.status <> :contractEnd")
    List<Employee> findExpiredContractsToUpdate(@Param("today") LocalDate today, @Param("contractEnd") EmployeeStatus contractEnd);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.currentProject WHERE e.profession = :p")
    List<Employee> findByProfessionWithCurrentProject(@Param("p") Profession p);

    @Query("SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.currentProject WHERE LOWER(e.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Employee> findByFullNameContainingIgnoreCaseWithCurrentProject(@Param("name") String name);

    List<Employee> findByProfession(Profession profession);
    List<Employee> findByStatus(EmployeeStatus status);
    List<Employee> findByProfessionAndStatus(Profession profession, EmployeeStatus status);
    boolean existsByEmail(String email);
    Optional<Employee> findByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<Employee> findByPhone(String phone);
    List<Employee> findByFullNameContainingIgnoreCase(String fullName);
}
