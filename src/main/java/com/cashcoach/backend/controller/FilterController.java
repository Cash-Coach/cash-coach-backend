package com.cashcoach.backend.controller;

import com.cashcoach.backend.dto.ExpenseDTO;
import com.cashcoach.backend.dto.FilterDTO;
import com.cashcoach.backend.dto.IncomeDTO;
import com.cashcoach.backend.service.ExpenseService;
import com.cashcoach.backend.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filterDTO) {
        LocalDate startDate = filterDTO.getStartDate() != null ? filterDTO.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filterDTO.getEndDate() != null ? filterDTO.getEndDate() : LocalDate.now();
        String keyword = filterDTO.getKeyword() != null ? filterDTO.getKeyword() : "";
        String sortField = filterDTO.getSortField() != null ? filterDTO.getSortField() : "date";
        Sort.Direction order = "asc".equalsIgnoreCase(filterDTO.getSortOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(order, sortField);

        if (filterDTO.getType().equals("income")) {
            List<IncomeDTO> incomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomes);
        } else if (filterDTO.getType().equals("expense")) {
            List<ExpenseDTO> expenses = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenses);
        } else {
            return ResponseEntity.badRequest().body("Invalid transaction type. Must be 'income' or 'expense'.");
        }


    }


}
