package com.khata.settings.basicSettings.operatingExpenses.services.impl;

import com.khata.auth.service.UserService;
import com.khata.exceptions.ResourceAlreadyExistsException;
import com.khata.exceptions.ResourceNotFoundException;
import com.khata.settings.basicSettings.operatingExpenses.dto.OperatingExpenseDTO;
import com.khata.settings.basicSettings.operatingExpenses.entity.OperatingExpense;
import com.khata.settings.basicSettings.operatingExpenses.repositories.OperatingExpenseRepo;
import com.khata.settings.basicSettings.operatingExpenses.services.OperatingExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class OperatingExpenseServiceImpl implements OperatingExpenseService {

    private final OperatingExpenseRepo operatingExpenseRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public List<OperatingExpenseDTO> upsertOperatingExpenses(List<OperatingExpenseDTO> operatingExpenseDTOs) {
        Integer currentUserId = userService.getCurrentUserId();
        validateDuplicateExpenseNamesInRequest(operatingExpenseDTOs);

        List<OperatingExpense> operatingExpenses = operatingExpenseDTOs.stream()
                .map(operatingExpenseDTO -> buildOperatingExpenseForUpsert(operatingExpenseDTO, currentUserId))
                .toList();

        List<OperatingExpense> savedOperatingExpenses = operatingExpenseRepo.saveAll(operatingExpenses);
        log.info("Operating expenses upserted | count={}", savedOperatingExpenses.size());
        return savedOperatingExpenses.stream()
                .map(operatingExpense -> modelMapper.map(operatingExpense, OperatingExpenseDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OperatingExpenseDTO> getOperatingExpenses(Pageable pageable) {
        Integer currentUserId = userService.getCurrentUserId();
        Page<OperatingExpense> operatingExpenses = operatingExpenseRepo.findByCreatedUserId(currentUserId, pageable);
        return operatingExpenses.map(operatingExpense -> modelMapper.map(operatingExpense, OperatingExpenseDTO.class));
    }

    @Override
    @Transactional
    public void deleteOperatingExpense(Integer operatingExpenseId) {
        Integer currentUserId = userService.getCurrentUserId();
        OperatingExpense operatingExpense = getOperatingExpenseEntityById(operatingExpenseId, currentUserId);
        operatingExpenseRepo.delete(operatingExpense);
        log.info("Operating expense deleted | id={}", operatingExpenseId);
    }

    private OperatingExpense getOperatingExpenseEntityById(Integer operatingExpenseId, Integer currentUserId) {
        return operatingExpenseRepo.findByIdAndCreatedUserId(operatingExpenseId, currentUserId).orElseThrow(
                () -> new ResourceNotFoundException("Operating expense", "id", operatingExpenseId));
    }

    private OperatingExpense buildOperatingExpenseForUpsert(OperatingExpenseDTO operatingExpenseDTO, Integer currentUserId) {
        OperatingExpense operatingExpense = operatingExpenseDTO.getId() == null
                ? new OperatingExpense()
                : getOperatingExpenseEntityById(operatingExpenseDTO.getId(), currentUserId);

        Optional<OperatingExpense> existingByExpenseName =
                operatingExpenseRepo.findByExpenseNameAndCreatedUserId(operatingExpenseDTO.getExpenseName(), currentUserId);
        if (existingByExpenseName.isPresent() && !existingByExpenseName.get().getId().equals(operatingExpenseDTO.getId())) {
            alreadyExists(operatingExpenseDTO.getExpenseName());
        }

        operatingExpense.setExpenseName(operatingExpenseDTO.getExpenseName());
        operatingExpense.setAmountPerPiece(operatingExpenseDTO.getAmountPerPiece());
        operatingExpense.setCreatedUserId(currentUserId);
        return operatingExpense;
    }

    private void validateDuplicateExpenseNamesInRequest(List<OperatingExpenseDTO> operatingExpenseDTOs) {
        Set<String> expenseNames = new HashSet<>();
        for (OperatingExpenseDTO operatingExpenseDTO : operatingExpenseDTOs) {
            String normalizedExpenseName = operatingExpenseDTO.getExpenseName().trim().toLowerCase();
            if (!expenseNames.add(normalizedExpenseName)) {
                alreadyExists(operatingExpenseDTO.getExpenseName());
            }
        }
    }

    private void alreadyExists(String expenseName) {
        throw new ResourceAlreadyExistsException("Operating expense", expenseName);
    }
}
