package UI;

import Config.DatabaseInitializer;
import Domain.Appointment;
import Domain.Patient;
import Repository.*;
import Repository.AppointmentRepositories.*;
import Repository.PatientRepositories.*;
import Service.*;
// Added Config, Validation modules
import Config.Settings;
import Validation.*;
import java.util.Scanner;

public class ConsoleUI {
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final Scanner scanner = new Scanner(System.in);

    // Added Reports
    private final ReportService reportService;
    private final ReportUI reportUI;

    public ConsoleUI() {

        // 1. Get Settings
        String repoType = Settings.getRepositoryType();
        String pFileName = Settings.getPatients();
        String aFileName = Settings.getAppointments();
        String dbURL = Settings.getDatabaseURL();

        // 2. Initialise Repositories (generic IRepository)
        IRepository<String, Patient> pRepo;
        IRepository<String, Appointment> aRepo;

        switch (repoType.toLowerCase()) {
            case "text" -> {
                pRepo = new PatientTextFileRepository(pFileName);
                aRepo = new AppointmentTextFileRepository(aFileName);
                System.out.println("Using text files.");
            }

            case "binary" -> {
                pRepo = new PatientBinaryFileRepository(pFileName);
                aRepo = new AppointmentBinaryFileRepository(aFileName);
                System.out.println("Using binary files.");
            }

            case "database" -> {
                PatientDBRepository pdbRepo = new PatientDBRepository();
                pdbRepo.setJDBC_URL(dbURL);
                pRepo = pdbRepo;

                AppointmentDBRepository adbRepo = new AppointmentDBRepository();
                adbRepo.setJDBC_URL(dbURL);
                aRepo = adbRepo;

                // Initialize database tables if they don't exist
                DatabaseInitializer.initializeDatabase(dbURL);

                System.out.println("Using SQLite database at: " + dbURL);
            }

            case "xml" -> {
                pRepo = new PatientXMLRepository(pFileName);
                aRepo = new AppointmentXMLRepository(aFileName);
                System.out.println("Using XML files.");
            }
            case "json" -> {
                pRepo = new PatientJSONRepository(pFileName);
                aRepo = new AppointmentJSONRepository(aFileName);
                System.out.println("Using JSON files.");
            }

            default -> {
                pRepo = new PatientRepository();
                aRepo = new AppointmentRepository();
                System.out.println("Using Memory Repositories (Default).");
            }

        }

        // 3. Initialise services with validators

        IValidator<Patient> patientValidator = new PatientValidator();
        IValidator<Appointment> appointmentValidator = new AppointmentValidator();

        this.patientService = new PatientService(pRepo, patientValidator);
        this.appointmentService = new AppointmentService(aRepo, appointmentValidator);

        this.reportService = new ReportService(patientService, appointmentService);
        this.reportUI = new ReportUI(reportService, scanner);
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Dentist Management ===");
            System.out.println("1. Manage Patients");
            System.out.println("2. Manage Appointments");
            System.out.println("3. Generate Reports");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            switch (scanner.nextLine()) {
                case "1" -> managePatients();
                case "2" -> manageAppointments();
                case "3" -> reportUI.start();
                case "0" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void managePatients() {
        String option;
        do {
            System.out.println("\n--- Patient Management ---");
            System.out.println("1. Add  2. Modify  3. Delete  4. List  5. Filter by Name  6. Filter by Email  0. Back");
            System.out.print("Choice: ");
            option = scanner.nextLine();
            try {
                switch (option) {
                    case "1" -> {
                        System.out.print("ID: "); String id = scanner.nextLine();
                        System.out.print("Name: "); String n = scanner.nextLine();
                        System.out.print("Email: "); String e = scanner.nextLine();
                        System.out.print("Phone: "); String p = scanner.nextLine();
                        patientService.addPatient(id, n, e, p);
                        System.out.println("Patient added successfully.");
                    }
                    case "2" -> {
                        System.out.print("ID: "); String id = scanner.nextLine();
                        System.out.print("Name: "); String n = scanner.nextLine();
                        System.out.print("Email: "); String e = scanner.nextLine();
                        System.out.print("Phone: "); String p = scanner.nextLine();
                        patientService.modifyPatient(id, n, e, p);
                        System.out.println("Patient modified successfully.");
                    }
                    case "3" -> {
                        System.out.print("ID to delete: ");
                        patientService.deletePatient(scanner.nextLine());
                        System.out.println("Patient deleted successfully.");
                    }
                    case "4" -> patientService.getAllPatients().forEach(System.out::println);
                    case "5" -> {
                        System.out.print("Name keyword: ");
                        patientService.filterByName(scanner.nextLine()).forEach(System.out::println);
                    }
                    case "6" -> {
                        System.out.print("Email domain: ");
                        patientService.filterByEmail(scanner.nextLine()).forEach(System.out::println);
                    }
                    case "0" -> {} // Back to main menu
                    default -> System.out.println("Invalid option.");
                }
            } catch (RepositoryException | IllegalArgumentException | ValidationException e) { // Catch ValidationException
                System.out.println("Error: " + e.getMessage());
            }
        } while (!option.equals("0"));
    }

    private void manageAppointments() {
        String option;
        do {
            System.out.println("\n--- Appointment Management ---");
            System.out.println("1. Add  2. Modify  3. Delete  4. List  5. Filter by Date  6. Filter by Patient  0. Back");
            System.out.print("Choice: ");
            option = scanner.nextLine();
            try {
                switch (option) {
                    case "1" -> {
                        System.out.print("ID: "); String id = scanner.nextLine();
                        System.out.print("Patient ID: "); String pid = scanner.nextLine();
                        System.out.print("Date & Time (DD-MM-YYYY HH:MM): ");
                        String dt = scanner.nextLine();
                        appointmentService.addAppointment(id, pid, dt);
                        System.out.println("Appointment added successfully.");
                    }
                    case "2" -> {
                        System.out.print("ID: "); String id = scanner.nextLine();
                        System.out.print("Patient ID: "); String pid = scanner.nextLine();
                        System.out.print("Date & Time (DD-MM-YYYY HH:MM): ");
                        String dt = scanner.nextLine();
                        appointmentService.modifyAppointment(id, pid, dt);
                        System.out.println("Appointment modified successfully.");
                    }
                    case "3" -> {
                        System.out.print("ID to delete: ");
                        appointmentService.deleteAppointment(scanner.nextLine());
                        System.out.println("Appointment deleted successfully.");
                    }
                    case "4" -> appointmentService.getAllAppointments().forEach(System.out::println);
                    case "5" -> {
                        System.out.print("Date (DD-MM-YYYY): ");
                        appointmentService.filterByDate(scanner.nextLine()).forEach(System.out::println);
                    }
                    case "6" -> {
                        System.out.print("Patient ID: ");
                        appointmentService.filterByPatient(scanner.nextLine()).forEach(System.out::println);
                    }
                    case "0" -> {} // Back to main menu
                    default -> System.out.println("Invalid option.");
                }
            } catch (RepositoryException | IllegalArgumentException | ValidationException e) { // Catch ValidationException
                System.out.println("Error: " + e.getMessage());
            }
        } while (!option.equals("0"));
    }
}
