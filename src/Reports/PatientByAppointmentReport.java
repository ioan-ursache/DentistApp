package Reports;

import Domain.Appointment;
import Domain.Patient;
import Service.AppointmentService;
import Service.PatientService;
import Repository.RepositoryException;

import java.util.Optional;
import java.util.stream.StreamSupport;

public class PatientByAppointmentReport implements IReport<String, String> {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public PatientByAppointmentReport(PatientService patientService,
                                      AppointmentService appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @Override
    public String getName() {
        return "patient_by_appointment";
    }

    @Override
    public String getDescription() {
        return "Shows the patient scheduled at a given appointment ID.";
    }

    @Override
    public String execute(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return "No appointment ID provided.";
        }
        String id = appointmentId.trim();

        Iterable<Appointment> allAppointments = appointmentService.getAllAppointments();
        Optional<Appointment> maybeAppointment =
                StreamSupport.stream(allAppointments.spliterator(), false)
                        .filter(a -> a.getID().equals(id))
                        .findFirst();

        if (maybeAppointment.isEmpty()) {
            return "No appointment found with ID: " + id;
        }

        Appointment appt = maybeAppointment.get();
        String patientID = appt.getPatientID();

        Iterable<Patient> allPatients;
        try {
            allPatients = patientService.getAllPatients();
        } catch (RepositoryException e) {
            return "Error retrieving patients: " + e.getMessage();
        }

        Optional<Patient> maybePatient =
                StreamSupport.stream(allPatients.spliterator(), false)
                        .filter(p -> p.getID().equals(patientID))
                        .findFirst();

        if (maybePatient.isEmpty()) {
            return "Appointment found, but patient with ID " + patientID + " not found.";
        }

        Patient p = maybePatient.get();
        return "=========== PATIENT BY APPOINTMENT ===========\n" +
                "Appointment ID: " + appt.getID() + "\n" +
                "Date & Time   : " + appt.getDatetime() + "\n" +
                "Patient ID    : " + p.getID() + "\n" +
                "Name          : " + p.getName() + "\n" +
                "Email         : " + p.getEmail() + "\n" +
                "Phone         : " + p.getNumber() + "\n" +
                "==============================================";
    }
}