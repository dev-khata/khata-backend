package com.khata.staff.employee.service.impl;

import com.khata.auth.service.UserService;
import com.khata.exceptions.ResourceAlreadyExistsException;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.settings.department.entity.Department;
import com.khata.settings.department.repositories.DepartmentRepo;
import com.khata.staff.employee.dto.EmployeeDTO;
import com.khata.staff.employee.dto.EmployeeDepartmentDTO;
import com.khata.staff.employee.entity.Employee;
import com.khata.staff.employee.entity.EmployeeDepartment;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepo employeeRepo;
    private final DepartmentRepo departmentRepo;
    private final EmployeeExcelExportService employeeExcelExportService;
    private final UserService userService;

    @Override
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        validateEmployeeCodeIsUnique(employeeDTO.getEmployeeCode(), null, currentUserId);
        validatePhoneNumberIsUnique(employeeDTO.getPhoneNumber(), null, currentUserId);
        validateDuplicateDepartmentsInRequest(employeeDTO.getDepartments());

        Employee employee = new Employee();
        employee.setCreatedUserId(currentUserId);
        setEmployeeFields(employee, employeeDTO);

        Employee savedEmployee = employeeRepo.save(employee);
        log.info("Employee created | id={} | code={}", savedEmployee.getId(), savedEmployee.getEmployeeCode());
        return toEmployeeDTO(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeDTO updateEmployee(Integer employeeId, EmployeeDTO employeeDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        Employee employee = getEmployeeEntityById(employeeId, currentUserId);
        validateEmployeeCodeIsUnique(employeeDTO.getEmployeeCode(), employeeId, currentUserId);
        validatePhoneNumberIsUnique(employeeDTO.getPhoneNumber(), employeeId, currentUserId);
        validateDuplicateDepartmentsInRequest(employeeDTO.getDepartments());

        employee.setEmployeeCode(employeeDTO.getEmployeeCode());
        employee.setFullName(employeeDTO.getFullName());
        employee.setPhoneNumber(employeeDTO.getPhoneNumber());
        employee.setAddress(employeeDTO.getAddress());
        employee.setJoiningDateInNepali(employeeDTO.getJoiningDateInNepali());
        employee.setJoiningDateInEnglish(employeeDTO.getJoiningDateInEnglish());
        employee.setActive(employeeDTO.getActive() == null || employeeDTO.getActive());
        syncDepartments(employee, employeeDTO.getDepartments());

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
        employee.setEmployeeCode(employeeDTO.getEmployeeCode());
        employee.setFullName(employeeDTO.getFullName());
        employee.setPhoneNumber(employeeDTO.getPhoneNumber());
        employee.setAddress(employeeDTO.getAddress());
        employee.setJoiningDateInNepali(employeeDTO.getJoiningDateInNepali());
        employee.setJoiningDateInEnglish(employeeDTO.getJoiningDateInEnglish());
        employee.setActive(employeeDTO.getActive() == null || employeeDTO.getActive());
        employeeDTO.getDepartments().forEach(departmentDTO ->
                employee.getDepartments().add(buildEmployeeDepartment(employee, departmentDTO)));
    }

    private EmployeeDepartment buildEmployeeDepartment(Employee employee, EmployeeDepartmentDTO departmentDTO) {
        Department department = getDepartmentEntityById(departmentDTO.getDepartmentId());
        EmployeeDepartment employeeDepartment = new EmployeeDepartment();
        employeeDepartment.setEmployee(employee);
        employeeDepartment.setDepartment(department);
        return employeeDepartment;
    }

    private void syncDepartments(Employee employee, List<EmployeeDepartmentDTO> departmentDTOs) {
        Set<Integer> requestedDepartmentIds = departmentDTOs.stream()
                .map(EmployeeDepartmentDTO::getDepartmentId)
                .collect(Collectors.toSet());

        Iterator<EmployeeDepartment> iterator = employee.getDepartments().iterator();
        while (iterator.hasNext()) {
            EmployeeDepartment existingDepartment = iterator.next();
            if (!requestedDepartmentIds.contains(existingDepartment.getDepartment().getId())) {
                iterator.remove();
            }
        }

        Map<Integer, EmployeeDepartment> existingDepartmentsById = employee.getDepartments().stream()
                .collect(Collectors.toMap(
                        employeeDepartment -> employeeDepartment.getDepartment().getId(),
                        Function.identity()));

        for (EmployeeDepartmentDTO departmentDTO : departmentDTOs) {
            if (!existingDepartmentsById.containsKey(departmentDTO.getDepartmentId())) {
                employee.getDepartments().add(buildEmployeeDepartment(employee, departmentDTO));
            }
        }
    }

    private Employee getEmployeeEntityById(Integer employeeId, Integer currentUserId) {
        return employeeRepo.findByIdAndCreatedUserId(employeeId, currentUserId).orElseThrow(
                () -> new ResourceNotFoundException("Employee", "id", employeeId)
        );
    }

    private Department getDepartmentEntityById(Integer departmentId) {
        return departmentRepo.findById(departmentId).orElseThrow(
                () -> new ResourceNotFoundException("Department", "id", departmentId)
        );
    }

    private void validateEmployeeCodeIsUnique(String employeeCode, Integer employeeId, Integer currentUserId) {
        Optional<Employee> existingEmployee = employeeRepo.findByEmployeeCodeAndCreatedUserId(employeeCode, currentUserId);
        if (existingEmployee.isPresent() && !existingEmployee.get().getId().equals(employeeId)) {
            throw new ResourceAlreadyExistsException("Employee code", employeeCode);
        }
    }

    private void validatePhoneNumberIsUnique(String phoneNumber, Integer employeeId, Integer currentUserId) {
        Optional<Employee> existingEmployee = employeeRepo.findByPhoneNumberAndCreatedUserId(phoneNumber, currentUserId);
        if (existingEmployee.isPresent() && !existingEmployee.get().getId().equals(employeeId)) {
            throw new ResourceAlreadyExistsException("Employee phone number", phoneNumber);
        }
    }

    private void validateDuplicateDepartmentsInRequest(List<EmployeeDepartmentDTO> departments) {
        Set<Integer> departmentIds = new HashSet<>();
        for (EmployeeDepartmentDTO department : departments) {
            if (!departmentIds.add(department.getDepartmentId())) {
                throw new ResourceAlreadyExistsException("Employee department", department.getDepartmentId());
            }
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
        employeeDTO.setDepartments(employee.getDepartments().stream()
                .map(this::toEmployeeDepartmentDTO)
                .toList());
        return employeeDTO;
    }

    private EmployeeDepartmentDTO toEmployeeDepartmentDTO(EmployeeDepartment employeeDepartment) {
        EmployeeDepartmentDTO departmentDTO = new EmployeeDepartmentDTO();
        departmentDTO.setDepartmentId(employeeDepartment.getDepartment().getId());
        departmentDTO.setDepartmentName(employeeDepartment.getDepartment().getDepartmentName());
        return departmentDTO;
    }
}
