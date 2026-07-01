package com.khata.settings.accountType.services.impl;

import com.khata.auth.service.UserService;
import com.khata.settings.accountType.dto.AccountTypeDTO;
import com.khata.settings.accountType.entity.AccountType;
import com.khata.settings.accountType.repositories.AccountTypeRepo;
import com.khata.settings.accountType.services.AccountTypeService;
import com.khata.exceptions.BadRequestException;
import com.khata.exceptions.ResourceAlreadyExistsException;
import com.khata.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class AccountTypeServiceImpl implements AccountTypeService {

    private final AccountTypeRepo accountTypeRepo;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public AccountTypeServiceImpl(AccountTypeRepo accountTypeRepo, ModelMapper modelMapper, UserService userService) {
        this.accountTypeRepo = accountTypeRepo;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @Override
    @Transactional
    public AccountTypeDTO createAccountType(AccountTypeDTO accountTypeDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        boolean exists = accountTypeRepo.existsVisibleByName(accountTypeDTO.getName(), currentUserId);
        if (exists) {
            alreadyExists(accountTypeDTO.getName());
        }

        AccountType accountType = modelMapper.map(accountTypeDTO, AccountType.class);
        accountType.setCreatedUserId(currentUserId);
        AccountType savedAccountType = accountTypeRepo.save(accountType);
        log.info("Account type created with name : {}", savedAccountType.getName());
        return modelMapper.map(savedAccountType, AccountTypeDTO.class);
    }

    @Override
    @Transactional
    public AccountTypeDTO updateAccountType(AccountTypeDTO accountTypeDTO, Integer accountTypeId) {
        Integer currentUserId = userService.getCurrentUserId();
        AccountType accountType = getAccountTypeEntityById(accountTypeId, currentUserId);

        checkIfSystemDefined(accountType, "updated");

        Optional<AccountType> existingByName = accountTypeRepo.findVisibleByName(
                accountTypeDTO.getName(),
                currentUserId);
        if (existingByName.isPresent() && !existingByName.get().getId().equals(accountTypeId)) {
            alreadyExists(accountTypeDTO.getName());
        }

        accountType.setName(accountTypeDTO.getName());
        accountType.setTransactionType(accountTypeDTO.getTransactionType());
        accountType.setDescription(accountTypeDTO.getDescription());

        AccountType updatedAccountType = accountTypeRepo.save(accountType);
        log.info("Account type updated with id : {}", accountTypeId);
        return modelMapper.map(updatedAccountType, AccountTypeDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountTypeDTO getAccountTypeById(Integer accountTypeId) {
        Integer currentUserId = userService.getCurrentUserId();
        AccountType accountType = getAccountTypeEntityById(accountTypeId, currentUserId);
        return modelMapper.map(accountType, AccountTypeDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountTypeDTO> getAccountTypes(Pageable pageable) {
        Integer currentUserId = userService.getCurrentUserId();
        Page<AccountType> accountTypes = accountTypeRepo.findVisibleByCreatedUserId(currentUserId, pageable);
        return accountTypes.map(accountType -> modelMapper.map(accountType, AccountTypeDTO.class));
    }

    @Override
    @Transactional
    public void deleteAccountType(Integer accountTypeId) {
        Integer currentUserId = userService.getCurrentUserId();
        AccountType accountType = getAccountTypeEntityById(accountTypeId, currentUserId);
        checkIfSystemDefined(accountType, "deleted");
        log.info("Account type deleted with id : {}", accountTypeId);
        accountTypeRepo.delete(accountType);
    }

    private AccountType getAccountTypeEntityById(Integer accountTypeId, Integer currentUserId) {
        return accountTypeRepo.findVisibleById(accountTypeId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("AccountType with", "id", accountTypeId));
    }

    private void checkIfSystemDefined(AccountType accountType, String action) {
        if (accountType.isSystemDefault()) {
            throw new BadRequestException("System-defined account type cannot be " + action + ".");
        }
    }

    private void alreadyExists(String name) {
        throw new ResourceAlreadyExistsException("Account type", name);
    }
}
