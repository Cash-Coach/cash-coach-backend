package com.cashcoach.backend.controller;

import com.cashcoach.backend.service.ExcelService;
import com.cashcoach.backend.service.ExpenseService;
import com.cashcoach.backend.service.IncomeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @GetMapping("/download/incomes")
    public void downloadIncomesExcel(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=income.xlsx");
        excelService.writeIncomesToExcel(resp.getOutputStream(), incomeService.getCurrentMonthIncomesForCurrentUser());
    }

    @GetMapping("/download/expenses")
    public void downloadExpensesExcel(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=expense.xlsx");
        excelService.writeExpensesToExcel(resp.getOutputStream(), expenseService.getCurrentMonthExpensesForCurrentUser());
    }

}
