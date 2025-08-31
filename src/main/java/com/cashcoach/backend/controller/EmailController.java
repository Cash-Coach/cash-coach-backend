package com.cashcoach.backend.controller;

import com.cashcoach.backend.entity.Profile;
import com.cashcoach.backend.service.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final ExcelService excelService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final EmailService emailService;
    private final ProfileService profileService;

    @GetMapping("/income-excel")
    public ResponseEntity<Void> emailIncomeExcel() throws IOException, MessagingException {
        Profile userProfile = profileService.getCurrentProfile();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeIncomesToExcel(baos, incomeService.getCurrentMonthIncomesForCurrentUser());
        String subject = "Your Income Report";
        String body = "Hey " + userProfile.getFullName() + ",<br><br>"
                + "As requested, here is your income report.";
        String fileName = "income_report.xlsx";
        emailService.sendEmailWithAttachment(userProfile.getEmail(), subject, body, baos.toByteArray(), fileName);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/expense-excel")
    public ResponseEntity<Void> emailExpenseExcel() throws IOException, MessagingException {
        Profile userProfile = profileService.getCurrentProfile();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeExpensesToExcel(baos, expenseService.getCurrentMonthExpensesForCurrentUser());
        String subject = "Your Expense Report";
        String body = "Hey " + userProfile.getFullName() + ",<br><br>"
                + "As requested, here is your expense report.";
        String fileName = "expense_report.xlsx";
        emailService.sendEmailWithAttachment(userProfile.getEmail(), subject, body, baos.toByteArray(), fileName);
        return ResponseEntity.ok(null);
    }



}
