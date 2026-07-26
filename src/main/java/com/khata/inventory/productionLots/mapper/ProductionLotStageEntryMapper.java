package com.khata.inventory.productionLots.mapper;

import com.khata.inventory.productionLots.dto.ProductionLotStageEntryDTO;
import com.khata.inventory.productionLots.dto.ProductionLotStageEntryEmployeeDTO;
import com.khata.inventory.productionLots.dto.ProductionLotStageSizeBreakdownDTO;
import com.khata.inventory.productionLots.entity.ProductionLotStageEntry;
import com.khata.inventory.productionLots.entity.ProductionLotStageEntryEmployee;
import com.khata.inventory.productionLots.entity.ProductionLotStageSizeBreakdown;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductionLotStageEntryMapper {

    public ProductionLotStageEntryDTO toDTO(ProductionLotStageEntry entry) {
        ProductionLotStageEntryDTO dto = new ProductionLotStageEntryDTO();
        dto.setId(entry.getId());
        dto.setProductionLotId(entry.getProductionLot().getId());
        dto.setProductId(entry.getProduct().getId());
        dto.setProductName(entry.getProduct().getProductName());
        dto.setProductionStageId(entry.getProductionStage().getId());
        dto.setProductionStageName(entry.getProductionStage().getStageName());
        if (entry.getDepartment() != null) {
            dto.setDepartmentId(entry.getDepartment().getId());
            dto.setDepartmentName(entry.getDepartment().getDepartmentName());
            dto.setDepartmentCode(entry.getDepartment().getDepartmentCode());
        }
        if (entry.getEmployee() != null) {
            dto.setEmployeeId(entry.getEmployee().getId());
            dto.setEmployeeName(entry.getEmployee().getFullName());
        }
        dto.setWorkType(entry.getWorkType());
        dto.setExpectedPieces(entry.getExpectedPieces());
        dto.setCompletedPieces(entry.getCompletedPieces());
        dto.setExtraPieces(entry.getExtraPieces());
        dto.setDamagePieces(entry.getDamagePieces());
        dto.setAddToMaster(entry.getAddToMaster());
        dto.setDeductFromMaster(entry.getDeductFromMaster());
        dto.setRemarks(entry.getRemarks());
        dto.setCompleted(entry.getCompleted());
        dto.setEmployees(entry.getEmployees().stream()
                .map(this::toDTO)
                .collect(Collectors.toList()));
        dto.setSizeBreakdowns(entry.getSizeBreakdowns().stream()
                .map(this::toDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    public ProductionLotStageEntryEmployeeDTO toDTO(ProductionLotStageEntryEmployee employeeEntry) {
        ProductionLotStageEntryEmployeeDTO dto = new ProductionLotStageEntryEmployeeDTO();
        dto.setId(employeeEntry.getId());
        if (employeeEntry.getDepartment() != null) {
            dto.setDepartmentId(employeeEntry.getDepartment().getId());
            dto.setDepartmentName(employeeEntry.getDepartment().getDepartmentName());
            dto.setDepartmentCode(employeeEntry.getDepartment().getDepartmentCode());
        }
        dto.setEmployeeId(employeeEntry.getEmployee().getId());
        dto.setEmployeeName(employeeEntry.getEmployee().getFullName());
        dto.setRoleCode(employeeEntry.getRoleCode());
        dto.setRoleName(employeeEntry.getRoleName());
        dto.setPieces(employeeEntry.getPieces());
        return dto;
    }

    public ProductionLotStageSizeBreakdownDTO toDTO(ProductionLotStageSizeBreakdown sizeBreakdown) {
        ProductionLotStageSizeBreakdownDTO dto = new ProductionLotStageSizeBreakdownDTO();
        dto.setId(sizeBreakdown.getId());
        dto.setSize(sizeBreakdown.getSize());
        dto.setPieces(sizeBreakdown.getPieces());
        return dto;
    }
}
