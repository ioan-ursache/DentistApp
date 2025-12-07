package Reports;

import Domain.Patient;
import Service.PatientService;
import Repository.RepositoryException;

import java.util.stream.StreamSupport;

public class PatientContactReport implements IReport<String, String> {

    private final PatientService patientService;

    public PatientContactReport(PatientService patientService) {
        this.patientService = patientService;
    }

    @Override
    public String getName() {
        return "patient_contact";
    }

    @Override
    public String getDescription() {
        return "Shows contact information (email, phone) for a given patient ID or exact name.";
    }

    @Override
    public String execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "No patient ID or name provided.";
        }
        String key = input.trim();

        Iterable<Patient> allPatients;
        try {
            allPatients = patientService.getAllPatients();
        } catch (RepositoryException e) {
            return "Error retrieving patients: " + e.getMessage();
        }

        return StreamSupport.stream(allPatients.spliterator(), false)
                .filter(p -> p.getID().equals(key) || p.getName().equalsIgnoreCase(key))
                .findFirst()
                .map(p -> "================ PATIENT CONTACT ================\n" +
                        "ID   : " + p.getID() + "\n" +
                        "Name : " + p.getName() + "\n" +
                        "Email: " + p.getEmail() + "\n" +
                        "Phone: " + p.getNumber() + "\n" +
                        "=================================================")
                .orElse("No patient found with ID or name: " + key);
    }
}