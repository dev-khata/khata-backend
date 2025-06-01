package com.khata.accountType.services.impl;

import com.khata.accountType.dto.AccountTypeDTO;
import com.khata.accountType.entity.AccountType;
import com.khata.accountType.repositories.AccountTypeRepo;
import com.khata.accountType.services.AccountTypeService;
import com.khata.accountType.util.AccountCategoryConstants;
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
        AccountType accountType = this.modelMapper.map(accountTypeDTO, AccountType.class);
        AccountType savedAccountType = this.accountTypeRepo.save(accountType);
        log.info("Account type created with title : {}", savedAccountType.getCategory().getName());
        return this.modelMapper.map(savedAccountType, AccountTypeDTO.class);
    }

    @Override
    @Transactional
    public AccountTypeDTO updateAccountType(AccountTypeDTO accountTypeDTO, Integer accountTypeId) {
        AccountType accountType = getAccountTypeEntityById(accountTypeId);
        String accountCategoryTypeName = accountType.getCategory().getName();

        if(!AccountCategoryConstants.isDefaultCategory(accountCategoryTypeName)){
            accountType.setCategory(accountTypeDTO.getCategory());
            accountType.setTransactionType(accountTypeDTO.getTransactionType());
            accountType.setDescription(accountTypeDTO.getDescription());
            AccountType updatedAccountType = this.accountTypeRepo.save(accountType);
            log.info("Account type updated with id : {}", accountTypeId);
            return this.modelMapper.map(updatedAccountType, AccountTypeDTO.class);
        }else{
            throw new UnsupportedOperationException("Updating default account type is not allowed");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccountTypeDTO getAccountTypeById(Integer accountTypeId) {
        AccountType accountType = getAccountTypeEntityById(accountTypeId);
        return this.modelMapper.map(accountType, AccountTypeDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountTypeDTO> getAccountTypes(Pageable pageable) {
        Page<AccountType> accountTypes = accountTypeRepo.findAll(pageable);
        return accountTypes.map(accountType -> modelMapper.map(accountType,AccountTypeDTO.class));
    }

    @Override
    public void deleteAccountType(Integer accountTypeId) {
        AccountType accountType = getAccountTypeEntityById(accountTypeId);
        String accountCategoryTypeName = accountType.getCategory().getName();
        if(!AccountCategoryConstants.isDefaultCategory(accountCategoryTypeName)){
            this.accountTypeRepo.delete(accountType);
        }else{
            throw new UnsupportedOperationException("Deleting default account type is not allowed");
        }
    }

    private AccountType getAccountTypeEntityById(Integer accountTypeId){
        return this.accountTypeRepo.findById(accountTypeId).orElseThrow(
                ()-> new ResourceNotFoundException("AccountType with", "id", accountTypeId));
    }
}
