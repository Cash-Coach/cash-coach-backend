package com.cashcoach.backend.controller;

import com.cashcoach.backend.dto.IncomeDTO;
import com.cashcoach.backend.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO incomeDTO) {
        IncomeDTO addedIncomeDTO = incomeService.addIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedIncomeDTO);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getIncomesForTheMonth() {
        List<IncomeDTO> incomes = incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.ok(incomes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }
    
}
