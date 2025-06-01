package com.khata.accountType.controller;

import com.khata.accountType.dto.AccountTypeDTO;
import com.khata.accountType.services.AccountTypeService;
import com.khata.payload.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account-type")
@AllArgsConstructor
public class AccountTypeController {

    private final AccountTypeService accountTypeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<AccountTypeDTO>> createAccountType(@Valid @RequestBody AccountTypeDTO accountTypeDTO){
        AccountTypeDTO accountType = accountTypeService.createAccountType(accountTypeDTO);
        ApiResponse<AccountTypeDTO> response = new ApiResponse<>(
                accountType,HttpStatus.CREATED.value(), "Account type created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AccountTypeDTO>>> getAccountTypes(Pageable pageable){
        Page<AccountTypeDTO> accountTypeDTOPage = accountTypeService.getAccountTypes(pageable);
        return ResponseEntity.ok(new ApiResponse<>(accountTypeDTOPage, HttpStatus.OK.value()));
    }

    @PutMapping("/{accountTypeId}")
    public ResponseEntity<ApiResponse<AccountTypeDTO>> updateAccountType(
            @Valid @RequestBody AccountTypeDTO accountTypeDTO,
            @PathVariable Integer accountTypeId){
        AccountTypeDTO accountType = accountTypeService.updateAccountType(accountTypeDTO, accountTypeId);
        ApiResponse<AccountTypeDTO> response = new ApiResponse<>(
                accountType, HttpStatus.OK.value(), "Account type updated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accountTypeId")
    public ResponseEntity<ApiResponse<AccountTypeDTO>> getAccountTypeDetails(
            @PathVariable Integer accountTypeId){
        AccountTypeDTO accountTypeDTO = accountTypeService.getAccountTypeById(accountTypeId);
        return ResponseEntity.ok(new ApiResponse<>(accountTypeDTO,HttpStatus.OK.value()));
    }

    @DeleteMapping("/{}accountTypeId")
    public ResponseEntity<ApiResponse<Void>> deleteAccountTypeById(
            @PathVariable Integer accountTypeId){
        accountTypeService.deleteAccountType(accountTypeId);
        ApiResponse<Void> response = new ApiResponse<>(null, HttpStatus.OK.value(), "Account type deleted successfully");
        return ResponseEntity.ok(response);
    }

}
