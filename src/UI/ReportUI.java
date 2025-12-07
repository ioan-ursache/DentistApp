package UI;

import Service.ReportService;

import java.util.Scanner;

public class ReportUI {

    private final ReportService reportService;
    private final Scanner scanner;

    public ReportUI(ReportService reportService, Scanner scanner) {
        this.reportService = reportService;
        this.scanner = scanner;
    }

    public void start() {
        String option;
        do {
            System.out.println("\n=== Reports Menu ===");
            System.out.println("Available reports:");
            System.out.println(reportService.listReports());
            System.out.println("\nCommands:");
            System.out.println("  run <report_name>  - execute a report");
            System.out.println("  0                  - back to main menu");
            System.out.print("Choice: ");

            option = scanner.nextLine().trim();

            if (option.equals("0")) {
                return;
            } else if (option.startsWith("run ")) {
                String reportName = option.substring(4).trim();
                if (!reportService.hasReport(reportName)) {
                    System.out.println("Unknown report: " + reportName);
                    continue;
                }
                // Ask for input parameter (if any)
                System.out.print("Enter input parameter (ID/name/date) or leave empty if not needed: ");
                String param = scanner.nextLine();
                String result = reportService.runReport(reportName, param);
                System.out.println("\n" + result);
            } else {
                System.out.println("Invalid command. Use 'run <report_name>' or '0' to go back.");
            }

        } while (true);
    }
}