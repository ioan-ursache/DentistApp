package Service;

import Reports.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Orchestrates all reports. Knows how to list them and execute by key.
 */
public class ReportService {

    private final Map<String, IReport<String, String>> reports = new LinkedHashMap<>();

    public ReportService(PatientService patientService, AppointmentService appointmentService) {
        // Register all reports here
        register(new PatientContactReport(patientService));
        register(new PatientByAppointmentReport(patientService, appointmentService));
        register(new AppointmentsByPatientNameReport(patientService, appointmentService));
        register(new PatientsByDateReport(patientService, appointmentService));
        register(new PatientStatisticsReport(patientService, appointmentService)); // bonus
    }

    private void register(IReport<String, String> report) {
        reports.put(report.getName(), report);
    }

    public String listReports() {
        return reports.values().stream()
                .map(r -> "- " + r.getName() + " : " + r.getDescription())
                .collect(Collectors.joining("\n"));
    }

    public boolean hasReport(String name) {
        return reports.containsKey(name);
    }

    public String runReport(String name, String input) {
        IReport<String, String> report = reports.get(name);
        if (report == null) {
            return "Unknown report: " + name;
        }
        return report.execute(input);
    }

    public Map<String, IReport<String, String>> getReports() {
        return reports;
    }
}