package com.khata.staff.employee.service;

import com.khata.staff.employee.entity.EmployeeDepartment;
import com.khata.staff.employee.entity.enums.EmployeePaymentType;
import com.khata.staff.employee.repositories.EmployeeDepartmentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeDepartmentAssignmentMigrationService implements ApplicationRunner {

    private final EmployeeDepartmentRepo employeeDepartmentRepo;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<EmployeeDepartment> assignments = employeeDepartmentRepo.findByPaymentTypeIsNull();
        if (assignments.isEmpty()) {
            return;
        }

        assignments.forEach(this::migrateAssignment);
        employeeDepartmentRepo.saveAll(assignments);
        log.info("Migrated {} employee department assignments", assignments.size());
    }

    private void migrateAssignment(EmployeeDepartment assignment) {
        EmployeePaymentType paymentType = Boolean.TRUE.equals(assignment.getDepartment().getPieceRateEnabled())
                ? EmployeePaymentType.PIECE_RATE
                : EmployeePaymentType.MONTHLY;
        assignment.setPaymentType(paymentType);
        assignment.setMonthlySalary(null);
    }
}
