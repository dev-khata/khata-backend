package com.khata.settings.chartOfAccount.services.impl;

import com.khata.auth.service.UserService;
import com.khata.settings.accountType.entity.AccountType;
import com.khata.settings.accountType.repositories.AccountTypeRepo;
import com.khata.settings.chartOfAccount.dto.ChartOfAccountDTO;
import com.khata.settings.chartOfAccount.entity.ChartOfAccount;
import com.khata.settings.chartOfAccount.repositories.ChartOfAccountRepo;
import com.khata.settings.chartOfAccount.services.ChartOfAccountServices;
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
public class ChartOfAccountServiceImpl implements ChartOfAccountServices {

    private final ModelMapper modelMapper;
    private final ChartOfAccountRepo chartOfAccountRepo;
    private final AccountTypeRepo accountTypeRepo;
    private final UserService userService;

    public ChartOfAccountServiceImpl(
            ModelMapper modelMapper,
            ChartOfAccountRepo chartOfAccountRepo,
            AccountTypeRepo accountTypeRepo,
            UserService userService) {
        this.modelMapper = modelMapper;
        this.chartOfAccountRepo = chartOfAccountRepo;
        this.accountTypeRepo = accountTypeRepo;
        this.userService = userService;
    }

    @Override
    @Transactional
    public ChartOfAccountDTO createChartOfAccount(ChartOfAccountDTO chartOfAccountDTO) {
        Integer currentUserId = userService.getCurrentUserId();
        boolean exists = chartOfAccountRepo.existsVisibleByName(chartOfAccountDTO.getName(), currentUserId);
        if (exists) {
            alreadyExists(chartOfAccountDTO.getName());
        }

        ChartOfAccount chartOfAccount = modelMapper.map(chartOfAccountDTO, ChartOfAccount.class);
        chartOfAccount.setCreatedUserId(currentUserId);
        Integer accountTypeId = chartOfAccountDTO.getAccountType().getId();
        AccountType accountType = getAccountTypeById(accountTypeId, currentUserId);
        chartOfAccount.setAccountType(accountType);
        ChartOfAccount savedAccountType = chartOfAccountRepo.save(chartOfAccount);

        log.info("Chart of account created with name : {}", savedAccountType.getName());
        return modelMapper.map(savedAccountType, ChartOfAccountDTO.class);
    }

    @Override
    @Transactional
    public ChartOfAccountDTO updateChartOfAccount(ChartOfAccountDTO chartOfAccountDTO, Integer chartOfAccountId) {
        Integer currentUserId = userService.getCurrentUserId();
        Optional<ChartOfAccount> existingByName = chartOfAccountRepo.findVisibleByName(
                chartOfAccountDTO.getName(),
                currentUserId);
        if (existingByName.isPresent() && !existingByName.get().getId().equals(chartOfAccountId)) {
            alreadyExists(chartOfAccountDTO.getName());
        }

        ChartOfAccount chartOfAccount = getChartOfAccountEntityById(chartOfAccountId, currentUserId);
        checkIfSystemDefined(chartOfAccount, "updated");
        chartOfAccount.setName(chartOfAccountDTO.getName());
        chartOfAccount.setDescription(chartOfAccountDTO.getDescription());

        Integer accountTypeId = chartOfAccountDTO.getAccountType().getId();
        AccountType accountType = getAccountTypeById(accountTypeId, currentUserId);
        chartOfAccount.setAccountType(accountType);

        chartOfAccount.setActive(chartOfAccountDTO.isActive());

        ChartOfAccount updatedChartOfAccount = chartOfAccountRepo.save(chartOfAccount);
        log.info("Chart of account updated with id : {}", chartOfAccountId);
        return modelMapper.map(updatedChartOfAccount, ChartOfAccountDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ChartOfAccountDTO getChartOfAccountById(Integer chartOfAccountId) {
        Integer currentUserId = userService.getCurrentUserId();
        ChartOfAccount chartOfAccount = getChartOfAccountEntityById(chartOfAccountId, currentUserId);
        return modelMapper.map(chartOfAccount, ChartOfAccountDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChartOfAccountDTO> getChartOfAccounts(Pageable pageable) {
        Integer currentUserId = userService.getCurrentUserId();
        Page<ChartOfAccount> chartOfAccounts = chartOfAccountRepo.findVisibleByCreatedUserId(currentUserId, pageable);
        return chartOfAccounts.map(chartOfAccount -> modelMapper.map(chartOfAccount, ChartOfAccountDTO.class));
    }

    @Override
    @Transactional
    public void deleteChartOfAccount(Integer chartOfAccountId) {
        Integer currentUserId = userService.getCurrentUserId();
        ChartOfAccount chartOfAccount = getChartOfAccountEntityById(chartOfAccountId, currentUserId);
        checkIfSystemDefined(chartOfAccount, "deleted");
        log.info("Chart of account deleted with id : {}", chartOfAccountId);
        chartOfAccountRepo.delete(chartOfAccount);
    }

    private ChartOfAccount getChartOfAccountEntityById(Integer chartOfAccountId, Integer currentUserId) {
        return chartOfAccountRepo.findVisibleById(chartOfAccountId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Chart of account", "id", chartOfAccountId));
    }

    private AccountType getAccountTypeById(Integer accountTypeId, Integer currentUserId) {
        return accountTypeRepo.findVisibleById(accountTypeId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Account type", "id", accountTypeId));
    }

    private void checkIfSystemDefined(ChartOfAccount chartOfAccount, String action) {
        if (chartOfAccount.isSystemDefault()) {
            throw new BadRequestException("System-defined chart of account cannot be " + action + ".");
        }
    }

    private void alreadyExists(String name) {
        throw new ResourceAlreadyExistsException("Chart of account", name);
    }
}
