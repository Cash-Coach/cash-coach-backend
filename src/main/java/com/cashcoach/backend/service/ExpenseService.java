package com.cashcoach.backend.service;

import com.cashcoach.backend.dto.ExpenseDTO;
import com.cashcoach.backend.entity.Category;
import com.cashcoach.backend.entity.Expense;
import com.cashcoach.backend.entity.Profile;
import com.cashcoach.backend.repository.CategoryRepository;
import com.cashcoach.backend.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ProfileService profileService;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    // Adds a new expense to db
    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        Profile userProfile = profileService.getCurrentProfile();
        Category category = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Expense newExpense = toEntity(expenseDTO, userProfile, category);
        newExpense = expenseRepository.save(newExpense);
        return toDTO(newExpense);
    }

    // Retrieve all expenses for current month/based on start date and end date
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
        Profile userProfile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<Expense> currMonthExpenses = expenseRepository.findByProfileIdAndDateBetween(userProfile.getId(), startDate, endDate);
        return currMonthExpenses.stream().map(this::toDTO).toList();
    }

    // Delete expense by id for current user
    public void deleteExpense(Long expenseId) {
        Profile userProfile = profileService.getCurrentProfile();
        Expense deletedExpense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense was not found"));
        if (!deletedExpense.getProfile().getId().equals(userProfile.getId())) {
            throw new RuntimeException("Not authorized to delete this expense");
        }
        expenseRepository.delete(deletedExpense);
    }

    // get 5 most recent expenses for current user
    public List<ExpenseDTO> get5MostRecentExpenses() {
        Profile userProfile = profileService.getCurrentProfile();
        List<Expense> top5Expenses = expenseRepository.findTop5ByProfileIdOrderByDateDesc(userProfile.getId());
        return top5Expenses.stream().map(this::toDTO).toList();
    }

    // get total expenses for current user
    public BigDecimal getTotalExpenses() {
        Profile userProfile = profileService.getCurrentProfile();
        BigDecimal totalExpense = expenseRepository.findTotalExpenseByProfileId(userProfile.getId());
        return totalExpense != null ? totalExpense : BigDecimal.ZERO;
    }

    // filter expenses
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        Profile userProfile = profileService.getCurrentProfile();
        List<Expense> filteredExpenses = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(userProfile.getId(), startDate, endDate, keyword, sort);
        return filteredExpenses.stream().map(this::toDTO).toList();
    }

    // Notifications
    public List<ExpenseDTO> getExpensesOnDate(Long profileId, LocalDate date) {
        List<Expense> expenses = expenseRepository.findByProfileIdAndDate(profileId, date);
        return expenses.stream().map(this::toDTO).toList();
    }

    // helpers
    private Expense toEntity(ExpenseDTO expenseDTO, Profile userProfile, Category category) {
        return Expense.builder()
                .name(expenseDTO.getName())
                .icon(expenseDTO.getIcon())
                .amount(expenseDTO.getAmount())
                .date(expenseDTO.getDate())
                .profile(userProfile)
                .category(category)
                .build();
    }

    private ExpenseDTO toDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .name(expense.getName())
                .icon(expense.getIcon())
                .categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null)
                .categoryName(expense.getCategory() != null ? expense.getCategory().getName() : "N/A")
                .amount(expense.getAmount())
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }

}
