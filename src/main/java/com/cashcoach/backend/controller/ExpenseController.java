package com.cashcoach.backend.controller;

import com.cashcoach.backend.dto.ExpenseDTO;
import com.cashcoach.backend.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO addedExpenseDTO = expenseService.addExpense(expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedExpenseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getExpensesForTheMonth() {
        List<ExpenseDTO> expenses = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseDTO> updateExpense(@PathVariable Long expenseId, @RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO updatedExpense = expenseService.updateExpense(expenseId, expenseDTO);
        return ResponseEntity.ok(updatedExpense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

}
