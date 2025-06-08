package com.khata.chartOfAccount.services.impl;

import com.khata.accountType.entity.AccountType;
import com.khata.accountType.repositories.AccountTypeRepo;
import com.khata.chartOfAccount.dto.ChartOfAccountDTO;
import com.khata.chartOfAccount.entity.ChartOfAccount;
import com.khata.chartOfAccount.repositories.ChartOfAccountRepo;
import com.khata.chartOfAccount.services.ChartOfAccountServices;
import com.khata.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ChartOfAccountServiceImpl implements ChartOfAccountServices {

    private final ModelMapper modelMapper;
    private final ChartOfAccountRepo chartOfAccountRepo;
    private final AccountTypeRepo accountTypeRepo;

    public ChartOfAccountServiceImpl(ModelMapper modelMapper, ChartOfAccountRepo chartOfAccountRepo, AccountTypeRepo accountTypeRepo) {
        this.modelMapper = modelMapper;
        this.chartOfAccountRepo = chartOfAccountRepo;
        this.accountTypeRepo = accountTypeRepo;

        // Configure ModelMapper to map AccountType's ID
        configureModelMapperForChartOfAccountToChartOfAccountDTO();
    }

    @Override
    @Transactional
    public ChartOfAccountDTO createChartOfAccount(ChartOfAccountDTO chartOfAccountDTO) {
        ChartOfAccount chartOfAccount = modelMapper.map(chartOfAccountDTO, ChartOfAccount.class);
        AccountType accountType = getAccountTypeById(chartOfAccountDTO.getAccountType());
        chartOfAccount.setAccountType(accountType);
        ChartOfAccount savedAccountType = chartOfAccountRepo.save(chartOfAccount);

        log.info("Chart of account created with name : {}", savedAccountType.getName());
        return modelMapper.map(savedAccountType, ChartOfAccountDTO.class);
    }

    @Override
    @Transactional
    public ChartOfAccountDTO updateChartOfAccount(ChartOfAccountDTO chartOfAccountDTO, Integer chartOfAccountId) {
        ChartOfAccount chartOfAccount = getChartOfAccountEntityById(chartOfAccountId);
        chartOfAccount.setName(chartOfAccountDTO.getName());
        chartOfAccount.setDescription(chartOfAccountDTO.getDescription());

        AccountType accountType = getAccountTypeById(chartOfAccountDTO.getAccountType());
        chartOfAccount.setAccountType(accountType);

        chartOfAccount.setSystemDefault(chartOfAccountDTO.isSystemDefault());
        chartOfAccount.setActive(chartOfAccountDTO.isActive());

        ChartOfAccount updatedChartOfAccount = chartOfAccountRepo.save(chartOfAccount);
        log.info("Chart of account updated with id : {}", chartOfAccountId);
        return modelMapper.map(updatedChartOfAccount, ChartOfAccountDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ChartOfAccountDTO getChartOfAccountById(Integer chartOfAccountId) {
        ChartOfAccount chartOfAccount = getChartOfAccountEntityById(chartOfAccountId);
        return modelMapper.map(chartOfAccount, ChartOfAccountDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChartOfAccountDTO> getChartOfAccounts(Pageable pageable) {
        Page<ChartOfAccount> chartOfAccounts = chartOfAccountRepo.findAll(pageable);
        return chartOfAccounts.map(chartOfAccount -> modelMapper.map(chartOfAccount, ChartOfAccountDTO.class));
    }

    @Override
    public void deleteChartOfAccount(Integer chartOfAccountId) {
        ChartOfAccount chartOfAccount = getChartOfAccountEntityById(chartOfAccountId);
        log.info("Chart of account deleted with id : {}", chartOfAccountId);
        chartOfAccountRepo.delete(chartOfAccount);
    }

    private ChartOfAccount getChartOfAccountEntityById(Integer chartOfAccountId) {
        return chartOfAccountRepo.findById(chartOfAccountId).orElseThrow(
                () -> new ResourceNotFoundException("Chart of account", "id", chartOfAccountId));
    }

    private AccountType getAccountTypeById(Integer accountTypeId){
        return accountTypeRepo.findById(accountTypeId).orElseThrow(
                () -> new ResourceNotFoundException("Chart of account", "id", accountTypeId));
    }

    private void configureModelMapperForChartOfAccountToChartOfAccountDTO(){
        this.modelMapper.typeMap(ChartOfAccount.class, ChartOfAccountDTO.class)
                .addMapping(src -> src.getAccountType().getId(), ChartOfAccountDTO::setAccountType);
    }
}
