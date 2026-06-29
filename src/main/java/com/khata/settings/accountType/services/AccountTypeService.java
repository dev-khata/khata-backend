package com.khata.settings.accountType.services;

import com.khata.settings.accountType.dto.AccountTypeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountTypeService {
    AccountTypeDTO createAccountType(AccountTypeDTO accountTypeDTO);
    AccountTypeDTO updateAccountType(AccountTypeDTO accountTypeDTO, Integer accountTypeId);
    AccountTypeDTO getAccountTypeById(Integer accountTypeId);
    Page<AccountTypeDTO> getAccountTypes(Pageable pageable);
    void deleteAccountType(Integer accountTypeId);
}
