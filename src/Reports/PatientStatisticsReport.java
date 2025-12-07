package Reports;

import Domain.Appointment;
import Domain.Patient;
import Service.AppointmentService;
import Service.PatientService;
import Repository.RepositoryException;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PatientStatisticsReport implements IReport<String, String> {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public PatientStatisticsReport(PatientService patientService,
                                   AppointmentService appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @Override
    public String getName() {
        return "patient_statistics";
    }

    @Override
    public String getDescription() {
        return "Shows basic statistics: total patients, total appointments, " +
                "average appointments per patient, and patient with most appointments.";
    }

    @Override
    public String execute(String ignoredInput) {
        Iterable<Patient> allPatients;
        try {
            allPatients = patientService.getAllPatients();
        } catch (RepositoryException e) {
            return "Error retrieving patients: " + e.getMessage();
        }
        Iterable<Appointment> allAppointments = appointmentService.getAllAppointments();

        long patientCount = StreamSupport.stream(allPatients.spliterator(), false).count();
        long appointmentCount = StreamSupport.stream(allAppointments.spliterator(), false).count();

        Map<String, Long> appointmentsPerPatient =
                StreamSupport.stream(allAppointments.spliterator(), false)
                        .collect(Collectors.groupingBy(Appointment::getPatientID, Collectors.counting()));

        double avgAppointmentsPerPatient =
                patientCount == 0 ? 0.0 :
                        (double) appointmentCount / (double) patientCount;

        Optional<Map.Entry<String, Long>> maxEntry =
                appointmentsPerPatient.entrySet().stream()
                        .max(Map.Entry.comparingByValue());

        StringBuilder sb = new StringBuilder();
        sb.append("=============== PATIENT STATISTICS ===============\n");
        sb.append("Total patients             : ").append(patientCount).append("\n");
        sb.append("Total appointments         : ").append(appointmentCount).append("\n");
        sb.append("Avg appointments/patient   : ")
                .append(String.format("%.2f", avgAppointmentsPerPatient)).append("\n");

        if (maxEntry.isPresent()) {
            String maxPatientID = maxEntry.get().getKey();
            long maxCount = maxEntry.get().getValue();

            Optional<Patient> maxPatient =
                    StreamSupport.stream(allPatients.spliterator(), false)
                            .filter(p -> p.getID().equals(maxPatientID))
                            .findFirst();

            if (maxPatient.isPresent()) {
                sb.append("Most appointments          : ")
                        .append(maxPatient.get().getName())
                        .append(" (ID=").append(maxPatientID)
                        .append(") with ").append(maxCount).append(" appointments.\n");
            } else {
                sb.append("Most appointments          : Patient ID ")
                        .append(maxPatientID).append(" with ")
                        .append(maxCount).append(" appointments (name unknown).\n");
            }
        } else {
            sb.append("Most appointments          : No appointments.\n");
        }

        sb.append("=================================================\n");
        return sb.toString();
    }
}