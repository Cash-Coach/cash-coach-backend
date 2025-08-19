package com.cashcoach.backend.service;

import com.cashcoach.backend.dto.ExpenseDTO;
import com.cashcoach.backend.dto.IncomeDTO;
import com.cashcoach.backend.dto.RecentTransactionDTO;
import com.cashcoach.backend.entity.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData() {
        Profile userProfile = profileService.getCurrentProfile();
        Map<String, Object> dashboardData = new LinkedHashMap<>();
        List<IncomeDTO> top5MostRecentIncomes = incomeService.get5MostRecentIncomes();
        List<ExpenseDTO> top5MostRecentExpenses = expenseService.get5MostRecentExpenses();

        List<RecentTransactionDTO> recentTransactions = Stream.concat(
                top5MostRecentIncomes.stream().map(
                        income -> RecentTransactionDTO.builder()
                                .id(income.getId())
                                .profileId(userProfile.getId())
                                .icon(income.getIcon())
                                .name(income.getName())
                                .amount(income.getAmount())
                                .date(income.getDate())
                                .createdAt(income.getCreatedAt())
                                .updatedAt(income.getUpdatedAt())
                                .type("income")
                                .build()
                ),
                top5MostRecentExpenses.stream().map(
                        expense -> RecentTransactionDTO.builder()
                                .id(expense.getId())
                                .profileId(userProfile.getId())
                                .icon(expense.getIcon())
                                .name(expense.getName())
                                .amount(expense.getAmount())
                                .date(expense.getDate())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .type("expense")
                                .build()
                )
        ).sorted((a,b) -> {
            int cmp = b.getDate().compareTo(a.getDate());
            if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            }
            return cmp;
        }).collect(Collectors.toList());

        dashboardData.put("totalBalance", incomeService.getTotalIncomes().subtract(expenseService.getTotalExpenses()));
        dashboardData.put("totalIncome", incomeService.getTotalIncomes());
        dashboardData.put("totalExpense", expenseService.getTotalExpenses());
        dashboardData.put("recent5Expenses", top5MostRecentExpenses);
        dashboardData.put("recent5Incomes", top5MostRecentIncomes);
        dashboardData.put("recentTransactions", recentTransactions);

        return dashboardData;

    }

}
