package Repository.PatientRepositories;

import Domain.Patient;
import Repository.JSONRepository;

/**
 * Patient JSON Repository - persists patients to JSON format.
 * Uses Jackson for automatic JSON serialization/deserialization.
 */
public class PatientJSONRepository extends JSONRepository<String, Patient> {

    public PatientJSONRepository(String jsonFilePath) {
        super(jsonFilePath, Patient.class);
        readFromFile();
    }
}
