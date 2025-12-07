package Reports;

import Domain.Appointment;
import Domain.Patient;
import Service.AppointmentService;
import Service.PatientService;
import Repository.RepositoryException;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PatientsByDateReport implements IReport<String, String> {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public PatientsByDateReport(PatientService patientService,
                                AppointmentService appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @Override
    public String getName() {
        return "patients_by_date";
    }

    @Override
    public String getDescription() {
        return "Lists patients who have appointments on a given date (DD-MM-YYYY).";
    }

    @Override
    public String execute(String date) {
        if (date == null || date.trim().isEmpty()) {
            return "No date provided.";
        }
        String d = date.trim();

        Iterable<Appointment> allAppointments = appointmentService.getAllAppointments();

        Map<String, java.util.List<Appointment>> apptsByPatient =
                StreamSupport.stream(allAppointments.spliterator(), false)
                        .filter(a -> a.getDatetime() != null &&
                                a.getDatetime().startsWith(d))
                        .collect(Collectors.groupingBy(Appointment::getPatientID));

        if (apptsByPatient.isEmpty()) {
            return "No appointments found on date: " + d;
        }

        Iterable<Patient> allPatients;
        try {
            allPatients = patientService.getAllPatients();
        } catch (RepositoryException e) {
            return "Error retrieving patients: " + e.getMessage();
        }

        return apptsByPatient.entrySet().stream()
                .map(entry -> {
                    String patientID = entry.getKey();
                    java.util.List<Appointment> appointments = entry.getValue();

                    Optional<Patient> maybePatient =
                            StreamSupport.stream(allPatients.spliterator(), false)
                                    .filter(p -> p.getID().equals(patientID))
                                    .findFirst();

                    String pname = maybePatient.map(Patient::getName).orElse("Unknown patient");
                    StringBuilder sb = new StringBuilder();
                    sb.append(pname).append(" (ID=").append(patientID).append("):\n");
                    appointments.stream()
                            .sorted((a1, a2) -> a1.getDatetime().compareTo(a2.getDatetime()))
                            .forEach(a -> sb.append("  - ")
                                    .append(a.getDatetime())
                                    .append(" [Appointment ID: ")
                                    .append(a.getID())
                                    .append("]\n"));
                    return sb.toString();
                })
                .collect(Collectors.joining("\n",
                        "====== PATIENTS WITH APPOINTMENTS ON " + d + " ======\n",
                        "=====================================================\n"));
    }
}