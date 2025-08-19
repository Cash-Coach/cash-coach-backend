package com.cashcoach.backend.service;

import com.cashcoach.backend.dto.ExpenseDTO;
import com.cashcoach.backend.entity.Profile;
import com.cashcoach.backend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${cash.coach.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 20 * * *", zone="EST") // email sent at 8pm everyday est
    public void sendDailyIncomeExpenseReminder() {
        log.info("Job started: sendDailyIncomeExpenseReminder()");
        List<Profile> allProfiles = profileRepository.findAll();
        for (Profile userProfile : allProfiles) {
            String body = "Hey " + userProfile.getFullName() + ",<br><br>"
                    + "I hope you've had a nice day so far! This is a friendly reminder to log your income and expenses on Cash Coach!<br><br>"
                    + "<a href=" + frontendUrl + " style='display:inline-block;padding:10px 20px;background-color:#4CAF50;color:#fff;"
                    + "text-decoration:none;border-radius:5px;font-weight:bold;'> Go to Cash Coach </a>"
                    + "<br><br>Regards,<br>Your Cash Coach";
            emailService.sendEmail(userProfile.getEmail(), "Daily reminder: Update your transactions!", body);
        }
        log.info("Job completed: sendDailyIncomeExpenseReminder()");
    }

    @Scheduled(cron = "0 0 21 * * *", zone="EST")
    public void sendDailyExpenseSummary() {
        log.info("Job started: sendDailyExpenseSummary()");
        List<Profile> allProfiles = profileRepository.findAll();
        for (Profile userProfile : allProfiles) {
           List<ExpenseDTO> expensesToday =expenseService.getExpensesOnDate(userProfile.getId(), LocalDate.now(ZoneId.of("America/New_York")));
            if (!expensesToday.isEmpty()) {
                StringBuilder table = new StringBuilder();
                table.append("<table style='border-collapse:collapse;width:100%;'>");
                table.append("<tr style='background-color:#f2f2f2;'><th style='border:1px solid #ddd;padding:8px;'>S.No</th><th style='border:1px solid #ddd;padding:8px;'>Name</th><th style='border:1px solid #ddd;padding:8px;'>Amount</th><th style='border:1px solid #ddd;padding:8px;'>Category</th></tr>");
                int ct = 1;
                for (ExpenseDTO expenseDTO : expensesToday) {
                    table.append("<tr>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(ct++).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expenseDTO.getName()).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expenseDTO.getAmount()).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expenseDTO.getCategoryId() != null ? expenseDTO.getCategoryName() : "N/A").append("</td>");
                    table.append("<tr>");
                }
                table.append("</table>");
                String body = "Hey " + userProfile.getFullName() + ",<br><br>Here is a summary of your expenses for today:<br/><br>"
                        + table + "<br>,<br>Regards,<br>Your Cash Coach";

                emailService.sendEmail(userProfile.getEmail(), "Your expenses from Cash Coach", body);
            }


        }
        log.info("Job completed: sendDailyExpenseSummary()");
    }
}
