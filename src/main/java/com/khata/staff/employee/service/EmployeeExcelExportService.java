package com.khata.staff.employee.service;

import com.khata.staff.employee.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeExcelExportService {

    private static final String[] EXPORT_HEADERS = {
            "Employee Code",
            "Full Name",
            "Phone Number",
            "Address",
            "Joining Date (Nepali)",
            "Joining Date (English)",
            "Active",
            "Departments"
    };

    public byte[] exportEmployees(List<Employee> employees) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Employees");
            writeHeaderRow(workbook, sheet);
            writeEmployeeRows(sheet, employees);
            autoSizeColumns(sheet);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            log.error("Employee export failed", ex);
            throw new IllegalStateException("Unable to export employees. Please try again.");
        }
    }

    private void writeHeaderRow(Workbook workbook, Sheet sheet) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        for (int columnIndex = 0; columnIndex < EXPORT_HEADERS.length; columnIndex++) {
            headerRow.createCell(columnIndex).setCellValue(EXPORT_HEADERS[columnIndex]);
            headerRow.getCell(columnIndex).setCellStyle(headerStyle);
        }
    }

    private void writeEmployeeRows(Sheet sheet, List<Employee> employees) {
        int rowIndex = 1;
        for (Employee employee : employees) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(employee.getEmployeeCode());
            row.createCell(1).setCellValue(employee.getFullName());
            row.createCell(2).setCellValue(employee.getPhoneNumber());
            row.createCell(3).setCellValue(employee.getAddress());
            row.createCell(4).setCellValue(employee.getJoiningDateInNepali());
            row.createCell(5).setCellValue(employee.getJoiningDateInEnglish().toString());
            row.createCell(6).setCellValue(Boolean.TRUE.equals(employee.getActive()) ? "Yes" : "No");
            row.createCell(7).setCellValue(formatDepartmentNames(employee));
        }
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int columnIndex = 0; columnIndex < EXPORT_HEADERS.length; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
        }
    }

    private String formatDepartmentNames(Employee employee) {
        return employee.getDepartments().stream()
                .map(employeeDepartment -> employeeDepartment.getDepartment().getDepartmentName())
                .collect(Collectors.joining(", "));
    }
}
