package com.khata.staff.employee.service.impl;

import com.khata.auth.service.UserService;
import com.khata.exceptions.BadRequestException;
import com.khata.exceptions.ResourceAlreadyExistsException;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.settings.department.entity.Department;
import com.khata.settings.department.repositories.DepartmentRepo;
import com.khata.staff.employee.dto.EmployeeDepartmentAssignmentDTO;
import com.khata.staff.employee.dto.EmployeeDTO;
import com.khata.staff.employee.entity.Employee;
import com.khata.staff.employee.entity.EmployeeDepartment;
import com.khata.staff.employee.entity.enums.EmployeePaymentType;
import com.khata.staff.employee.repositories.EmployeeRepo;
import com.khata.staff.employee.service.EmployeeExcelExportService;
import com.khata.staff.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final String DEFAULT_EMPLOYEE_CODE_PREFIX = "EMP";
    private static final int PADDED_EMPLOYEE_CODE_SUFFIX_LIMIT = 999;

    private final EmployeeRepo employeeRepo;
    private final DepartmentRepo departmentRepo;
    private final EmployeeExcelExportService employeeExcelExportService;
    private final UserService userService;

    @Override
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        validatePhoneNumberIsUnique(employeeDTO.getPhoneNumber(), null, currentUserId);
        validateDepartmentAssignments(employeeDTO.getDepartmentAssignments(), currentUserId);

        Employee employee = new Employee();
        employee.setCreatedUserId(currentUserId);
        employee.setEmployeeCode(generateEmployeeCode(employeeDTO.getFullName(), currentUserId));
        setEmployeeFields(employee, employeeDTO);
        addDepartmentAssignments(employee, employeeDTO.getDepartmentAssignments(), currentUserId);

        Employee savedEmployee = employeeRepo.save(employee);
        log.info("Employee created | id={} | code={}", savedEmployee.getId(), savedEmployee.getEmployeeCode());
        return toEmployeeDTO(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeDTO updateEmployee(Integer employeeId, EmployeeDTO employeeDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        Employee employee = getEmployeeEntityById(employeeId, currentUserId);
        validatePhoneNumberIsUnique(employeeDTO.getPhoneNumber(), employeeId, currentUserId);
        validateDepartmentAssignments(employeeDTO.getDepartmentAssignments(), currentUserId);

        setEmployeeFields(employee, employeeDTO);
        syncDepartmentAssignments(employee, employeeDTO.getDepartmentAssignments(), currentUserId);

        Employee updatedEmployee = employeeRepo.save(employee);
        log.info("Employee updated | id={}", employeeId);
        return toEmployeeDTO(updatedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeById(Integer employeeId) {
        Integer currentUserId = userService.getCurrentUserId();
        Employee employee = getEmployeeEntityById(employeeId, currentUserId);
        return toEmployeeDTO(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeDTO> getEmployees(Pageable pageable) {
        Integer currentUserId = userService.getCurrentUserId();
        Page<Employee> employees = employeeRepo.findByCreatedUserId(currentUserId, pageable);
        return employees.map(this::toEmployeeDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeDTO> searchEmployeeByName(String keyword, Pageable pageable) {
        Integer currentUserId = userService.getCurrentUserId();
        Page<Employee> employees = employeeRepo.findByFullNameContainingIgnoreCaseAndCreatedUserId(
                keyword, currentUserId, pageable);
        return employees.map(this::toEmployeeDTO);
    }

    @Override
    @Transactional
    public void deleteEmployee(Integer employeeId) {
        Integer currentUserId = userService.getCurrentUserId();
        Employee employee = getEmployeeEntityById(employeeId, currentUserId);
        employeeRepo.delete(employee);
        log.info("Employee deleted | id={}", employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportEmployees() {
        Integer currentUserId = userService.getCurrentUserId();
        List<Employee> employees = employeeRepo.findAllByCreatedUserIdOrderByEmployeeCodeAsc(currentUserId);
        return employeeExcelExportService.exportEmployees(employees);
    }

    private void setEmployeeFields(Employee employee, EmployeeDTO employeeDTO) {
        employee.setFullName(employeeDTO.getFullName());
        employee.setPhoneNumber(employeeDTO.getPhoneNumber());
        employee.setAddress(employeeDTO.getAddress());
        employee.setJoiningDateInNepali(employeeDTO.getJoiningDateInNepali());
        employee.setJoiningDateInEnglish(employeeDTO.getJoiningDateInEnglish());
        employee.setActive(employeeDTO.getActive() == null || employeeDTO.getActive());
    }

    private void addDepartmentAssignments(
            Employee employee,
            List<EmployeeDepartmentAssignmentDTO> assignmentDTOs,
            Integer currentUserId) {
        assignmentDTOs.forEach(assignmentDTO ->
                employee.getDepartmentAssignments().add(
                        buildEmployeeDepartmentAssignment(employee, assignmentDTO, currentUserId)));
    }

    private EmployeeDepartment buildEmployeeDepartmentAssignment(
            Employee employee,
            EmployeeDepartmentAssignmentDTO assignmentDTO,
            Integer currentUserId) {
        Department department = getDepartmentEntityById(assignmentDTO.getDepartmentId(), currentUserId);
        EmployeeDepartment assignment = new EmployeeDepartment();
        assignment.setEmployee(employee);
        assignment.setDepartment(department);
        setAssignmentFields(assignment, assignmentDTO);
        return assignment;
    }

    private void syncDepartmentAssignments(
            Employee employee,
            List<EmployeeDepartmentAssignmentDTO> assignmentDTOs,
            Integer currentUserId) {
        Set<Integer> requestedDepartmentIds = assignmentDTOs.stream()
                .map(EmployeeDepartmentAssignmentDTO::getDepartmentId)
                .collect(Collectors.toSet());

        Iterator<EmployeeDepartment> iterator = employee.getDepartmentAssignments().iterator();
        while (iterator.hasNext()) {
            EmployeeDepartment existingAssignment = iterator.next();
            if (!requestedDepartmentIds.contains(existingAssignment.getDepartment().getId())) {
                iterator.remove();
            }
        }

        Map<Integer, EmployeeDepartment> existingAssignmentsByDepartmentId = employee.getDepartmentAssignments().stream()
                .collect(Collectors.toMap(
                        employeeDepartment -> employeeDepartment.getDepartment().getId(),
                        Function.identity()));

        for (EmployeeDepartmentAssignmentDTO assignmentDTO : assignmentDTOs) {
            EmployeeDepartment existingAssignment = existingAssignmentsByDepartmentId.get(assignmentDTO.getDepartmentId());
            if (existingAssignment == null) {
                employee.getDepartmentAssignments().add(
                        buildEmployeeDepartmentAssignment(employee, assignmentDTO, currentUserId));
            } else {
                setAssignmentFields(existingAssignment, assignmentDTO);
            }
        }
    }

    private void setAssignmentFields(EmployeeDepartment assignment, EmployeeDepartmentAssignmentDTO assignmentDTO) {
        assignment.setPaymentType(assignmentDTO.getPaymentType());
        assignment.setMonthlySalary(assignmentDTO.getPaymentType() == EmployeePaymentType.MONTHLY
                ? assignmentDTO.getMonthlySalary()
                : null);
    }

    private Employee getEmployeeEntityById(Integer employeeId, Integer currentUserId) {
        return employeeRepo.findByIdAndCreatedUserId(employeeId, currentUserId).orElseThrow(
                () -> new ResourceNotFoundException("Employee", "id", employeeId)
        );
    }

    private Department getDepartmentEntityById(Integer departmentId, Integer currentUserId) {
        return departmentRepo.findByIdAndCreatedUserId(departmentId, currentUserId).orElseThrow(
                () -> new ResourceNotFoundException("Department", "id", departmentId)
        );
    }

    private void validatePhoneNumberIsUnique(String phoneNumber, Integer employeeId, Integer currentUserId) {
        Optional<Employee> existingEmployee = employeeRepo.findByPhoneNumberAndCreatedUserId(phoneNumber, currentUserId);
        if (existingEmployee.isPresent() && !existingEmployee.get().getId().equals(employeeId)) {
            throw new ResourceAlreadyExistsException("Employee phone number", phoneNumber);
        }
    }

    private String generateEmployeeCode(String fullName, Integer currentUserId) {
        Integer maxSuffix = employeeRepo.findMaxEmployeeCodeSuffixByCreatedUserId(currentUserId);
        int nextSuffix = (maxSuffix == null ? 0 : maxSuffix) + 1;
        String suffix = nextSuffix <= PADDED_EMPLOYEE_CODE_SUFFIX_LIMIT
                ? String.format("%03d", nextSuffix)
                : String.valueOf(nextSuffix);
        return getEmployeeCodePrefix(fullName) + "-" + suffix;
    }

    private String getEmployeeCodePrefix(String fullName) {
        String firstWord = fullName.strip().split("\\s+")[0];
        String prefix = firstWord.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
        return prefix.isBlank() ? DEFAULT_EMPLOYEE_CODE_PREFIX : prefix;
    }

    private void validateDepartmentAssignments(List<EmployeeDepartmentAssignmentDTO> assignments, Integer currentUserId) {
        Set<Integer> departmentIds = new HashSet<>();
        for (EmployeeDepartmentAssignmentDTO assignment : assignments) {
            if (!departmentIds.add(assignment.getDepartmentId())) {
                throw new ResourceAlreadyExistsException("Employee department assignment", assignment.getDepartmentId());
            }
            Department department = getDepartmentEntityById(assignment.getDepartmentId(), currentUserId);
            validatePaymentFields(assignment, department);
        }
    }

    private void validatePaymentFields(EmployeeDepartmentAssignmentDTO assignment, Department department) {
        boolean pieceRateEnabled = Boolean.TRUE.equals(department.getPieceRateEnabled());
        if (pieceRateEnabled && assignment.getPaymentType() != EmployeePaymentType.PIECE_RATE) {
            throw new BadRequestException("Payment type must be PIECE_RATE for piece-rate enabled department.");
        }
        if (!pieceRateEnabled && assignment.getPaymentType() != EmployeePaymentType.MONTHLY) {
            throw new BadRequestException("Payment type must be MONTHLY for department without piece rate enabled.");
        }
        if (!pieceRateEnabled && assignment.getMonthlySalary() == null) {
            throw new BadRequestException("Monthly salary is required when payment type is MONTHLY.");
        }
    }

    private EmployeeDTO toEmployeeDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employee.getId());
        employeeDTO.setEmployeeCode(employee.getEmployeeCode());
        employeeDTO.setFullName(employee.getFullName());
        employeeDTO.setPhoneNumber(employee.getPhoneNumber());
        employeeDTO.setAddress(employee.getAddress());
        employeeDTO.setJoiningDateInNepali(employee.getJoiningDateInNepali());
        employeeDTO.setJoiningDateInEnglish(employee.getJoiningDateInEnglish());
        employeeDTO.setActive(employee.getActive());
        employeeDTO.setDepartmentAssignments(employee.getDepartmentAssignments().stream()
                .map(this::toEmployeeDepartmentAssignmentDTO)
                .toList());
        return employeeDTO;
    }

    private EmployeeDepartmentAssignmentDTO toEmployeeDepartmentAssignmentDTO(EmployeeDepartment employeeDepartment) {
        EmployeeDepartmentAssignmentDTO assignmentDTO = new EmployeeDepartmentAssignmentDTO();
        assignmentDTO.setDepartmentId(employeeDepartment.getDepartment().getId());
        assignmentDTO.setDepartmentName(employeeDepartment.getDepartment().getDepartmentName());
        assignmentDTO.setPaymentType(resolvePaymentType(employeeDepartment));
        assignmentDTO.setMonthlySalary(assignmentDTO.getPaymentType() == EmployeePaymentType.MONTHLY
                ? employeeDepartment.getMonthlySalary()
                : null);
        return assignmentDTO;
    }

    private EmployeePaymentType resolvePaymentType(EmployeeDepartment employeeDepartment) {
        if (employeeDepartment.getPaymentType() != null) {
            return employeeDepartment.getPaymentType();
        }
        return Boolean.TRUE.equals(employeeDepartment.getDepartment().getPieceRateEnabled())
                ? EmployeePaymentType.PIECE_RATE
                : EmployeePaymentType.MONTHLY;
    }
}
