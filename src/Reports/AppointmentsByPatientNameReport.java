package Reports;

import Domain.Appointment;
import Domain.Patient;
import Service.AppointmentService;
import Service.PatientService;
import Repository.RepositoryException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AppointmentsByPatientNameReport implements IReport<String, String> {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public AppointmentsByPatientNameReport(PatientService patientService,
                                           AppointmentService appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @Override
    public String getName() {
        return "appointments_by_patient_name";
    }

    @Override
    public String getDescription() {
        return "Lists all appointments for a patient, searched by exact name (case-insensitive).";
    }

    @Override
    public String execute(String patientName) {
        if (patientName == null || patientName.trim().isEmpty()) {
            return "No patient name provided.";
        }
        String name = patientName.trim();

        Iterable<Patient> allPatients;
        try {
            allPatients = patientService.getAllPatients();
        } catch (RepositoryException e) {
            return "Error retrieving patients: " + e.getMessage();
        }

        List<Patient> matchingPatients =
                StreamSupport.stream(allPatients.spliterator(), false)
                        .filter(p -> p.getName().equalsIgnoreCase(name))
                        .collect(Collectors.toList());

        if (matchingPatients.isEmpty()) {
            return "No patient found with name: " + name;
        }

        Iterable<Appointment> allAppointments = appointmentService.getAllAppointments();

        StringBuilder sb = new StringBuilder();
        sb.append("======= APPOINTMENTS FOR PATIENT NAME: ").append(name).append(" =======\n");

        for (Patient p : matchingPatients) {
            String pid = p.getID();
            List<Appointment> appointmentsForPatient =
                    StreamSupport.stream(allAppointments.spliterator(), false)
                            .filter(a -> a.getPatientID().equals(pid))
                            .sorted((a1, a2) -> a1.getDatetime().compareTo(a2.getDatetime()))
                            .collect(Collectors.toList());

            if (appointmentsForPatient.isEmpty()) {
                sb.append("Patient ").append(p.getName())
                        .append(" (ID=").append(p.getID()).append(") has no appointments.\n");
            } else {
                sb.append("Patient ").append(p.getName())
                        .append(" (ID=").append(p.getID()).append("):\n");
                appointmentsForPatient.forEach(a ->
                        sb.append("  - ").append(a.getDatetime())
                                .append(" [Appointment ID: ").append(a.getID()).append("]\n"));
            }
            sb.append("\n");
        }

        sb.append("============================================================");
        return sb.toString();
    }
}