package Repository.AppointmentRepositories;

import Domain.Appointment;
import Repository.JSONRepository;

/**
 * Appointment JSON Repository - persists appointments to JSON format.
 * Uses Jackson for automatic JSON serialization/deserialization.
 */
public class AppointmentJSONRepository extends JSONRepository<String, Appointment> {

    public AppointmentJSONRepository(String jsonFilePath) {
        super(jsonFilePath, Appointment.class);
        readFromFile();
    }
}
