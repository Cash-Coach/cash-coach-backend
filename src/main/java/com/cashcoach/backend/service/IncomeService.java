package com.cashcoach.backend.service;

import com.cashcoach.backend.dto.IncomeDTO;
import com.cashcoach.backend.dto.IncomeDTO;
import com.cashcoach.backend.entity.*;
import com.cashcoach.backend.entity.Income;
import com.cashcoach.backend.repository.CategoryRepository;
import com.cashcoach.backend.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final ProfileService profileService;
    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;

    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        Profile userProfile = profileService.getCurrentProfile();
        Category category = categoryRepository.findById(incomeDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Income newIncome = toEntity(incomeDTO, userProfile, category);
        newIncome = incomeRepository.save(newIncome);
        return toDTO(newIncome);
    }

    // Retrieve all incomes for current month/based on start date and end date
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        Profile userProfile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<Income> currMonthIncomes = incomeRepository.findByProfileIdAndDateBetween(userProfile.getId(), startDate, endDate);
        return currMonthIncomes.stream().map(this::toDTO).toList();
    }

    // Delete income by id for current user
    public void deleteIncome(Long incomeId) {
        Profile userProfile = profileService.getCurrentProfile();
        Income deletedIncome = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income was not found"));
        if (!deletedIncome.getProfile().getId().equals(userProfile.getId())) {
            throw new RuntimeException("Not authorized to delete this income");
        }
        incomeRepository.delete(deletedIncome);
    }

    // get 5 most recent incomes for current user
    public List<IncomeDTO> get5MostRecentIncomes() {
        Profile userProfile = profileService.getCurrentProfile();
        List<Income> top5incomes = incomeRepository.findTop5ByProfileIdOrderByDateDesc(userProfile.getId());
        return top5incomes.stream().map(this::toDTO).toList();
    }

    // get total incomes for current user
    public BigDecimal getTotalIncomes() {
        Profile userProfile = profileService.getCurrentProfile();
        BigDecimal totalIncome = incomeRepository.findTotalIncomeByProfileId(userProfile.getId());
        return totalIncome != null ? totalIncome : BigDecimal.ZERO;
    }

    // filter incomes
    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        Profile userProfile = profileService.getCurrentProfile();
        List<Income> filteredIncomes = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(userProfile.getId(), startDate, endDate, keyword, sort);
        return filteredIncomes.stream().map(this::toDTO).toList();
    }
    
    // helpers
    private Income toEntity(IncomeDTO incomeDTO, Profile userProfile, Category category) {
        return Income.builder()
                .name(incomeDTO.getName())
                .icon(incomeDTO.getIcon())
                .amount(incomeDTO.getAmount())
                .date(incomeDTO.getDate())
                .profile(userProfile)
                .category(category)
                .build();
    }

    private IncomeDTO toDTO(Income income) {
        return IncomeDTO.builder()
                .id(income.getId())
                .name(income.getName())
                .icon(income.getIcon())
                .categoryId(income.getCategory() != null ? income.getCategory().getId() : null)
                .categoryName(income.getCategory() != null ? income.getCategory().getName() : "N/A")
                .amount(income.getAmount())
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }

}
