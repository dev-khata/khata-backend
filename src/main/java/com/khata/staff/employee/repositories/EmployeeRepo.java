package com.khata.staff.employee.repositories;

import com.khata.staff.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByIdAndCreatedUserId(Integer id, Integer createdUserId);

    Optional<Employee> findByPhoneNumberAndCreatedUserId(String phoneNumber, Integer createdUserId);

    @Query(value = """
            select coalesce(max(cast(substring(employee_code from '-([0-9]+)$') as integer)), 0)
            from employee
            where created_user_id = :createdUserId
              and employee_code ~ '-[0-9]+$'
            """, nativeQuery = true)
    Integer findMaxEmployeeCodeSuffixByCreatedUserId(@Param("createdUserId") Integer createdUserId);

    Page<Employee> findByCreatedUserId(Integer createdUserId, Pageable pageable);

    List<Employee> findAllByCreatedUserIdOrderByEmployeeCodeAsc(Integer createdUserId);

    Page<Employee> findByFullNameContainingIgnoreCaseAndCreatedUserId(
            String fullName,
            Integer createdUserId,
            Pageable pageable);
}
