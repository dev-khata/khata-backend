package com.khata.accountType.services.impl;

import com.khata.accountType.dto.AccountTypeDTO;
import com.khata.accountType.entity.AccountType;
import com.khata.accountType.repositories.AccountTypeRepo;
import com.khata.accountType.services.AccountTypeService;
import com.khata.accountType.util.SystemAccountTypes;
import com.khata.exceptions.BadRequestException;
import com.khata.exceptions.ResourceAlreadyExistsException;
import com.khata.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountTypeServiceImpl implements AccountTypeService {

    private final AccountTypeRepo accountTypeRepo;
    private final ModelMapper modelMapper;

    public AccountTypeServiceImpl(AccountTypeRepo accountTypeRepo, ModelMapper modelMapper) {
        this.accountTypeRepo = accountTypeRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public AccountTypeDTO createAccountType(AccountTypeDTO accountTypeDTO) {
        boolean exists = accountTypeRepo.existsByName(accountTypeDTO.getName());
        if (exists) {
            alreadyExists(accountTypeDTO.getName());
        }

        AccountType accountType = modelMapper.map(accountTypeDTO, AccountType.class);
        AccountType savedAccountType = accountTypeRepo.save(accountType);
        log.info("Account type created with name : {}", savedAccountType.getName());
        return modelMapper.map(savedAccountType, AccountTypeDTO.class);
    }

    @Override
    @Transactional
    public AccountTypeDTO updateAccountType(AccountTypeDTO accountTypeDTO, Integer accountTypeId) {
        AccountType accountType = getAccountTypeEntityById(accountTypeId);

        checkIfSystemDefined(accountType, "updated");

        boolean exists = accountTypeRepo.existsByName(accountTypeDTO.getName());
        if (exists) {
            alreadyExists(accountTypeDTO.getName());
        }

        accountType.setName(accountTypeDTO.getName());
        accountType.setTransactionType(accountTypeDTO.getTransactionType());
        accountType.setDescription(accountTypeDTO.getDescription());
        accountType.setSystemDefault(accountTypeDTO.isSystemDefault());

        AccountType updatedAccountType = accountTypeRepo.save(accountType);
        log.info("Account type updated with id : {}", accountTypeId);
        return modelMapper.map(updatedAccountType, AccountTypeDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountTypeDTO getAccountTypeById(Integer accountTypeId) {
        AccountType accountType = getAccountTypeEntityById(accountTypeId);
        return modelMapper.map(accountType, AccountTypeDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountTypeDTO> getAccountTypes(Pageable pageable) {
        Page<AccountType> accountTypes = accountTypeRepo.findAll(pageable);
        return accountTypes.map(accountType -> modelMapper.map(accountType, AccountTypeDTO.class));
    }

    @Override
    public void deleteAccountType(Integer accountTypeId) {
        AccountType accountType = getAccountTypeEntityById(accountTypeId);
        checkIfSystemDefined(accountType, "deleted");
        log.info("Account type deleted with id : {}", accountTypeId);
        accountTypeRepo.delete(accountType);
    }

    private AccountType getAccountTypeEntityById(Integer accountTypeId) {
        return accountTypeRepo.findById(accountTypeId).orElseThrow(
                () -> new ResourceNotFoundException("AccountType with", "id", accountTypeId));
    }

    private void checkIfSystemDefined(AccountType accountType, String action) {
        if (accountType.isSystemDefault()) {
            throw new BadRequestException("System-defined account type cannot be " + action + ".");
        }
    }

    private void alreadyExists(String name){
        throw new ResourceAlreadyExistsException("Account type", name);
    }

}
